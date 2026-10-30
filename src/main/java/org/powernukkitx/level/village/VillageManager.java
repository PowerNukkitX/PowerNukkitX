package org.powernukkitx.level.village;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockBed;
import org.powernukkitx.block.BlockBell;
import org.powernukkitx.event.block.BellRingEvent;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.data.profession.Profession;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.entity.mob.EntityIllager;
import org.powernukkitx.entity.mob.EntityRavager;
import org.powernukkitx.entity.passive.EntityVillagerV2;
import org.powernukkitx.event.entity.CreatureSpawnEvent;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.command.utils.RawText;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.BossBarColor;
import org.powernukkitx.utils.DummyBossBar;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

import javax.annotation.Nullable;

/**
 * Villagers claim POIs from within {@link org.powernukkitx.level.format.Chunk#initChunk()}, which runs while the
 * chunk monitor is held. To keep the manager monitor a leaf lock, no method synchronized on this manager may
 * trigger a chunk load - block reads below are always non-loading.
 */
public final class VillageManager {
    public static final int INITIAL_HORIZONTAL_RADIUS = 32;
    public static final int INITIAL_VERTICAL_RADIUS = 12;
    public static final int HORIZONTAL_EXPANSION_RANGE = 32;
    public static final int VERTICAL_EXPANSION_RANGE = 52;

    public static final int RAID_OMEN_DURATION = 600;
    public static final int BELL_RING_MIN_INTERVAL = 60;
    public static final int BELL_RING_INTERVAL_SPREAD = 40;
    public static final int RAID_PREPARATION_TIME = 300;
    public static final int GROUP_COMPLETE_DELAY = 40;
    public static final int ALLOWED_SPAWN_FAILURES = 5;
    public static final int RAID_BOUNDS_PADDING = 32;
    public static final int RAID_TIMEOUT = 48000;
    public static final int RAID_SPAWN_MIN_RADIUS = 32;
    public static final int RAID_SPAWN_MAX_RADIUS = 48;
    public static final int RAID_SPAWN_ATTEMPTS = 10;
    public static final int VILLAGE_HERO_DURATION = 40 * 60 * 20;
    public static final int CELEBRATION_DURATION = 30 * 20;
    public static final int RAID_HELP_INTERVAL = 600;
    private static final int RAID_RECRUIT_INTERVAL = 20;
    private static final int RAID_RECRUIT_RANGE = 16;

    private static final String[] RAIDER_TYPES = {
            EntityID.PILLAGER, EntityID.VINDICATOR, EntityID.RAVAGER, EntityID.WITCH, EntityID.EVOCATION_ILLAGER
    };

    /**
     * How many of each {@link #RAIDER_TYPES} every group is made of, the first row being the first
     * group. Easy stops after three groups, normal after five, hard runs the seven. The two
     * ravagers that carry a rider in the last groups count as a ravager plus the rider on foot.
     */
    private static final int[][] RAID_GROUPS = {
            {4, 0, 0, 0, 0},
            {3, 2, 0, 0, 0},
            {3, 0, 1, 0, 0},
            {3, 0, 0, 3, 0},
            {1, 4, 2, 0, 1},
            {5, 2, 0, 0, 1},
            {0, 6, 1, 1, 3}
    };

    private static final int[] RAID_GROUP_RIDERS = {-1, -1, -1, -1, 0, -1, 4};

    private final Level level;
    private final ConcurrentHashMap<UUID, Village> villages = new ConcurrentHashMap<>();
    private final Map<UUID, Map<UUID, DummyBossBar>> raidBossBars = new HashMap<>();
    private record JobSiteCache(Set<String> ids, int version) {
    }

    private static volatile JobSiteCache jobSiteCache;

    private static Set<String> getJobSiteBlockIds() {
        int version = Profession.getRegistrationVersion();
        var cache = jobSiteCache;
        if (cache != null && cache.version() == version) {
            return cache.ids();
        }
        var rebuilt = new HashSet<String>();
        for (Profession profession : Profession.getProfessions().values()) {
            rebuilt.add(profession.getBlockID());
        }
        var ids = Set.copyOf(rebuilt);
        jobSiteCache = new JobSiteCache(ids, version);
        return ids;
    }

    private static @Nullable Profession getJobSiteProfession(String blockId) {
        for (Profession profession : Profession.getProfessions().values()) {
            if (profession.getBlockID().equals(blockId)) return profession;
        }
        return null;
    }

    private @Nullable VillagePoi createPoi(PoiType type, BlockVector3 position) {
        if (type != PoiType.ACQUIRABLE_JOB_SITE) return new VillagePoi(type, position);
        Profession profession = getJobSiteProfession(level.getBlock(position.x, position.y, position.z, false).getId());
        return profession == null ? null : VillagePoi.jobSite(profession, position);
    }

    private static BlockVector3 getPoiPosition(Block block, PoiType type) {
        BlockVector3 position = block.asBlockVector3();
        if (type == PoiType.HOME && block instanceof BlockBed bed && !bed.isHeadPiece()) {
            return position.getSide(bed.getBlockFace());
        }
        return position;
    }

    public VillageManager(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

    public List<Village> getVillages() {
        return List.copyOf(villages.values());
    }

    public Village getVillage(UUID uuid) {
        return villages.get(uuid);
    }

    public boolean isDweller(long entityId) {
        for (Village village : villages.values()) {
            if (containsDweller(village, entityId)) {
                return true;
            }
        }
        return false;
    }

    public Optional<Village> getVillageForDweller(long entityId) {
        for (Village village : villages.values()) {
            if (containsDweller(village, entityId)) {
                return Optional.of(village);
            }
        }
        return Optional.empty();
    }

    private static boolean containsDweller(Village village, long entityId) {
        for (VillageDwellers.Dweller dweller : village.dwellers().dwellers()) {
            for (VillageDwellers.Actor actor : dweller.actors()) {
                if (actor.id() == entityId) {
                    return true;
                }
            }
        }
        return false;
    }

    public Optional<Village> getVillageAt(BlockVector3 position) {
        for (Village village : villages.values()) {
            if (isInside(village.info(), position)) {
                return Optional.of(village);
            }
        }
        return Optional.empty();
    }

    /**
     * Creates a village with the Bedrock initial boundary centered on {@code center}.
     * A naturally formed village uses the pillow of its first claimed bed as this center.
     */
    public synchronized Village createVillage(BlockVector3 center) {
        BlockVector3 min = center.add(-INITIAL_HORIZONTAL_RADIUS, -INITIAL_VERTICAL_RADIUS, -INITIAL_HORIZONTAL_RADIUS);
        BlockVector3 max = center.add(INITIAL_HORIZONTAL_RADIUS, INITIAL_VERTICAL_RADIUS, INITIAL_HORIZONTAL_RADIUS);
        BlockVector3 raidMin = new BlockVector3(0, 0, 0);
        BlockVector3 raidMax = new BlockVector3(1, 1, 1);
        VillageInfo info = new VillageInfo(0, 0, false, 0, 0, raidMin, raidMax, 0, (byte) 1, min, max);
        Village village = new Village(UUID.randomUUID(), VillageDwellers.empty(), info,
                new VillagePlayers(new ListTag<Tag>()), discoverPois(min, max), null);
        villages.put(village.uuid(), village);
        return village;
    }

    private VillagePois discoverPois(BlockVector3 min, BlockVector3 max) {
        List<VillagePoi> pois = new ArrayList<>();
        var jobSiteIds = getJobSiteBlockIds();
        for (int chunkX = min.x >> 4; chunkX <= max.x >> 4; chunkX++) {
            for (int chunkZ = min.z >> 4; chunkZ <= max.z >> 4; chunkZ++) {
                if (level.getChunkIfLoaded(chunkX, chunkZ) == null) {
                    continue;
                }
                int minX = Math.max(min.x, chunkX << 4);
                int maxX = Math.min(max.x, (chunkX << 4) + 15);
                int minZ = Math.max(min.z, chunkZ << 4);
                int maxZ = Math.min(max.z, (chunkZ << 4) + 15);
                for (int x = minX; x <= maxX; x++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        for (int y = min.y; y <= max.y; y++) {
                            Block block = level.getBlock(x, y, z, false);
                            PoiType type = null;
                            if (block instanceof BlockBed bed) {
                                if (!bed.isHeadPiece()) continue;
                                type = PoiType.HOME;
                            } else if (block instanceof BlockBell) {
                                type = PoiType.MEETING;
                            } else if (jobSiteIds.contains(block.getId())) {
                                type = PoiType.ACQUIRABLE_JOB_SITE;
                            }
                            if (type != null) {
                                VillagePoi poi = createPoi(type, getPoiPosition(block, type));
                                if (poi != null) pois.add(poi);
                            }
                        }
                    }
                }
            }
        }
        return pois.isEmpty()
                ? new VillagePois(List.of())
                : new VillagePois(List.of(new VillagePoiGroup(-1, pois)));
    }

    public synchronized void addDweller(UUID villageUuid, VillageDwellers.Actor actor) {
        Village village = villages.get(villageUuid);
        if (village == null || isDweller(actor.id())) return;
        village.dwellers().get(VillageDwellers.Role.INHABITANT).actors().add(actor);
        if (findGroup(village.pois().poi(), actor.id()) < 0) village.pois().poi().add(new VillagePoiGroup(actor.id(), List.of()));
        if (!village.info().initialized()) village.setInfo(withInitialized(village.info()));
    }

    /**
     * Removes a permanently lost villager from population tracking without loading chunks.
     * Must not be called for an ordinary chunk unload.
     *
     * @param entityId persistent actor ID of the dead, removed or transformed villager
     */
    public synchronized void removeDweller(long entityId) {
        for (Village village : villages.values()) {
            village.dwellers().get(VillageDwellers.Role.INHABITANT).actors()
                    .removeIf(actor -> actor.id() == entityId);
        }
    }

    public void addVillage(Village village) {
        Village previous = villages.put(village.uuid(), village);
        if (previous != null && previous != village) {
            stopRaid(previous);
        }
    }

    public boolean removeVillage(Village village) {
        if (!villages.remove(village.uuid(), village)) {
            return false;
        }
        stopRaid(village);
        return true;
    }

    public Village removeVillage(UUID uuid) {
        Village removed = villages.remove(uuid);
        if (removed != null) {
            stopRaid(removed);
        }
        return removed;
    }

    public void clear() {
        for (Village village : villages.values()) {
            stopRaid(village);
        }
        villages.clear();
    }

    public void load(Collection<Village> villages) {
        clear();
        villages.forEach(village -> this.villages.put(village.uuid(), village));
    }

    /**
     * Advances the raids of this level. A raid starts when a player carrying bad omen walks into a
     * village, then each group is placed in turn, watched until it is wiped out, and the rewards
     * are handed out once the last one falls.
     */
    public void tick(long currentTick) {
        if (villages.isEmpty()) {
            return;
        }
        for (Village village : villages.values()) {
            VillageRaid raid = village.raid();
            if (raid == null) {
                if (currentTick % 20 == 0) {
                    gainRaidOmen(village);
                }
                continue;
            }
            village.setRaid(tickRaid(village, raid));
            setDwellersHiding(village, village.raid() != null
                    && village.raid().status() == VillageRaid.STATUS_ONGOING && !village.raid().isFinished());
            updateBossBars(village);
            if (village.raid() == null && village.houseCount() == 0) {
                dissolveVillage(village);
            }
        }
    }

    private void gainRaidOmen(Village village) {
        for (Player player : level.getPlayers().values()) {
            if (player.getEffect(EffectType.BAD_OMEN) == null
                    || player.getEffect(EffectType.RAID_OMEN) != null
                    || !isInside(village.info(), player.asBlockVector3())) {
                continue;
            }
            player.removeEffect(EffectType.BAD_OMEN);
            player.addEffect(Effect.get(EffectType.RAID_OMEN)
                    .setDuration(RAID_OMEN_DURATION)
                    .setVisible(true));

            player.sendEffectAnimation(EffectType.RAID_OMEN);
        }
    }

    /**
     * Raids the village the player stands in, if it can be raided at all. Called when the omen that
     * precedes a raid runs out.
     */
    public void triggerRaid(Player player) {
        if (level.getServer().getDifficulty() == 0) {
            return;
        }
        Village village = getVillageAt(player.asBlockVector3()).orElse(null);
        if (village == null || !village.isValid() || village.raid() != null) {
            return;
        }
        VillageInfo info = village.info();
        village.setInfo(info.withRaidBounds(
                info.boundsMin().add(-RAID_BOUNDS_PADDING, -RAID_BOUNDS_PADDING, -RAID_BOUNDS_PADDING),
                info.boundsMax().add(RAID_BOUNDS_PADDING, RAID_BOUNDS_PADDING, RAID_BOUNDS_PADDING)));

        BlockVector3 center = village.center();
        village.setRaid(VillageRaid.starting(level.getCurrentTick(), groupCount(),
                new Vector3(center.x + 0.5, center.y, center.z + 0.5)));
    }

    private int groupCount() {
        return switch (level.getServer().getDifficulty()) {
            case 1 -> 3;
            case 2 -> 5;
            default -> 7;
        };
    }

    private @Nullable VillageRaid tickRaid(Village village, VillageRaid raid) {
        if (raid.isFinished()) {
            removeBossBars(village);
            if (raid.status() != VillageRaid.STATUS_LOSS) {
                expireRaiders(raid, false);
            }
            village.raiderPositions().clear();
            return null;
        }
        if (raid.state() == VillageRaid.STATE_CELEBRATING) {
            long ticks = raid.ticks() + 1;
            return ticks < CELEBRATION_DURATION
                    ? raid.withTicks(ticks)
                    : raid.withState(VillageRaid.STATE_FINISHED);
        }
        if (!village.isValid()) {
            return loseRaid(raid);
        }
        restoreRaiderTargets(village, raid);
        if (!hasPlayerInRaidBounds(village)) {
            return raid;
        }
        return switch (raid.state()) {
            case VillageRaid.STATE_PREPARATION -> tickPreparation(village, raid);
            case VillageRaid.STATE_PICKING_SPAWN_POINT -> tickPickingSpawnPoint(village, raid);
            case VillageRaid.STATE_SPAWNING_GROUP -> tickSpawningGroup(village, raid);
            case VillageRaid.STATE_GROUP_IN_PLAY -> tickGroupInPlay(village, raid);
            case VillageRaid.STATE_AWARDING_REWARDS -> awardRewards(village, raid);
            default -> raid.withStatus(VillageRaid.STATUS_STOPPED);
        };
    }

    private VillageRaid loseRaid(VillageRaid raid) {
        expireRaiders(raid, true);
        return raid.withStatus(VillageRaid.STATUS_LOSS).withTicks(0);
    }

    private void playRaidHorn(Village village) {
        BlockVector3 center = village.center();
        level.addLevelSoundEvent(new Vector3(center.x + 0.5, center.y, center.z + 0.5),
                SoundEvent.RAID_HORN);
    }

    private void ringBells(Village village) {
        for (VillagePoiGroup group : village.pois().poi()) {
            for (VillagePoi poi : group.instances()) {
                if (poi.type() != PoiType.MEETING) {
                    continue;
                }
                BlockVector3 position = poi.position();
                if (level.getChunkIfLoaded(position.x >> 4, position.z >> 4) == null) {
                    continue;
                }
                if (level.getBlock(position.x, position.y, position.z, false) instanceof BlockBell bell) {
                    bell.ring(null, BellRingEvent.RingCause.UNKNOWN, null);
                }
            }
        }
    }

    private VillageRaid tickPreparation(Village village, VillageRaid raid) {
        long ticks = raid.ticks() + 1;
        if (ticks == 1) {
            playRaidHorn(village);
        }
        long currentTick = level.getCurrentTick();
        if (currentTick > village.bellRingTick()) {
            village.setBellRingTick(currentTick + BELL_RING_MIN_INTERVAL
                    + ThreadLocalRandom.current().nextInt(BELL_RING_INTERVAL_SPREAD));
            ringBells(village);
        }
        return ticks >= RAID_PREPARATION_TIME
                ? raid.withState(VillageRaid.STATE_PICKING_SPAWN_POINT)
                : raid.withTicks(ticks);
    }

    private VillageRaid tickPickingSpawnPoint(Village village, VillageRaid raid) {
        Vector3 spawnPoint = findSpawnPoint(village);
        if (spawnPoint != null) {
            return raid.withSpawnPoint(spawnPoint, raid.spawnFails()).withState(VillageRaid.STATE_SPAWNING_GROUP);
        }
        int spawnFails = raid.spawnFails() + 1;
        return spawnFails > ALLOWED_SPAWN_FAILURES
                ? raid.withStatus(VillageRaid.STATUS_STOPPED)
                : raid.withSpawnPoint(raid.spawnPosition(), spawnFails);
    }

    private VillageRaid tickSpawningGroup(Village village, VillageRaid raid) {
        List<Long> raiders = spawnGroup(village, raid);
        VillageRaid next = recruitRaiders(village,
                raid.withGroup(raid.groupNumber(), raiders, totalMaxHealth(raiders)), true);
        if (!next.raiders().isEmpty()) {
            for (long id : next.raiders()) {
                Entity raider = level.getEntityByUniqueId(id);
                if (raider != null) next.raiderHealth().put(id, raider.getHealth());
            }
            return next;
        }
        int spawnFails = raid.spawnFails() + 1;
        return spawnFails > ALLOWED_SPAWN_FAILURES
                ? raid.withStatus(VillageRaid.STATUS_STOPPED)
                : raid.withSpawnPoint(raid.spawnPosition(), spawnFails)
                        .withState(VillageRaid.STATE_PICKING_SPAWN_POINT);
    }

    private VillageRaid tickGroupInPlay(Village village, VillageRaid raid) {
        if (level.getCurrentTick() % RAID_RECRUIT_INTERVAL == 0) {
            raid = recruitRaiders(village, raid, false);
        }
        List<Long> alive = new ArrayList<>();
        for (long raiderId : raid.raiders()) {
            Entity raider = level.getEntityByUniqueId(raiderId);
            if (raider != null) {
                if (raider.isClosed() || !raider.isAlive()) {
                    village.raiderPositions().remove(raiderId);
                    raid.raiderHealth().remove(raiderId);
                    continue;
                }
                village.raiderPositions().put(raiderId, raider.asBlockVector3());
                alive.add(raiderId);
                continue;
            }
            BlockVector3 last = village.raiderPositions().get(raiderId);
            if (last == null || level.getChunkIfLoaded(last.x >> 4, last.z >> 4) == null) {
                alive.add(raiderId);
                continue;
            }
            village.raiderPositions().remove(raiderId);
            raid.raiderHealth().remove(raiderId);
        }
        if (!alive.isEmpty()) {
            if (alive.size() < raid.raiders().size()) {
                village.resetRaidHelp();
            } else if (village.tickRaidHelp()) {
                playRaidHelp(alive);
            }
            long inPlay = raid.ticks() + 1;
            if (inPlay < RAID_TIMEOUT) {
                return raid.withRaiders(alive).withTicks(inPlay);
            }
            for (Player player : level.getPlayers().values()) {
                if (isInsideRaidBounds(village, player)) {
                    player.sendRawTextMessage(RawText.fromRawText(
                            "{\"rawtext\":[{\"translate\":\"raid.expiry\"}]}"));
                }
            }
            return raid.withStatus(VillageRaid.STATUS_STOPPED);
        }
        if (!raid.raiders().isEmpty()) {
            return raid.withRaiders(alive).withTicks(0);
        }
        long ticks = raid.ticks() + 1;
        if (ticks < GROUP_COMPLETE_DELAY) {
            return raid.withRaiders(alive).withTicks(ticks);
        }
        return raid.completeGroup();
    }

    private void playRaidHelp(List<Long> raiders) {
        Entity selected = null;
        int count = 0;
        for (long id : raiders) {
            Entity raider = level.getEntityByUniqueId(id);
            if (raider != null && raider.isAlive() && !raider.isClosed()
                    && ThreadLocalRandom.current().nextInt(++count) == 0) selected = raider;
        }
        if (selected != null) level.addLevelSoundEvent(selected, SoundEvent.RAID_HORN);
    }

    private VillageRaid recruitRaiders(Village village, VillageRaid raid, boolean insideVillage) {
        List<Long> members = new ArrayList<>(raid.raiders());
        List<Entity> candidates = new ArrayList<>();
        if (insideVillage) {
            BlockVector3 min = village.info().boundsMin();
            BlockVector3 max = village.info().boundsMax();
            candidates.addAll(level.fastNearbyEntities(new SimpleAxisAlignedBB(
                    min.x, min.y, min.z, max.x + 1, max.y + 1, max.z + 1), null, false));
        } else {
            for (long id : raid.raiders()) {
                Entity member = level.getEntityByUniqueId(id);
                if (member == null || !member.isAlive() || member.isClosed()) continue;
                candidates.addAll(level.fastNearbyEntities(new SimpleAxisAlignedBB(
                        member.x - RAID_RECRUIT_RANGE, member.y - RAID_RECRUIT_RANGE, member.z - RAID_RECRUIT_RANGE,
                        member.x + RAID_RECRUIT_RANGE, member.y + RAID_RECRUIT_RANGE, member.z + RAID_RECRUIT_RANGE),
                        member, false));
            }
        }
        float maxHealth = 0;
        int recruits = 0;
        for (Entity candidate : candidates) {
            if (!(candidate instanceof EntityIntelligent raider) || !isRaiderType(raider)
                    || !raider.isAlive() || raider.isClosed() || raider.isBaby()
                    || raider.getLevel() != level || members.contains(raider.uniqueIdLong())
                    || !raider.getNbt().getString("RaidVillageUUID").isEmpty()
                    || raider.getMemoryStorage().get(CoreMemoryTypes.CELEBRATING)) continue;
            configureRaider(village, raider);
            members.add(raider.uniqueIdLong());
            raid.raiderHealth().put(raider.uniqueIdLong(), raider.getHealth());
            village.raiderPositions().put(raider.uniqueIdLong(), raider.asBlockVector3());
            maxHealth += raider.getMaxHealth();
            recruits++;
        }
        return recruits == 0 ? raid : raid.withRecruits(members, recruits, maxHealth);
    }

    private static boolean isRaiderType(Entity entity) {
        return switch (entity.getIdentifier()) {
            case EntityID.PILLAGER, EntityID.VINDICATOR, EntityID.RAVAGER,
                 EntityID.WITCH, EntityID.EVOCATION_ILLAGER -> true;
            default -> false;
        };
    }

    /**
     * Checks membership of an ongoing raid in this level without loading chunks.
     * Must be called on the owning level's tick thread.
     *
     * @param entity actor whose persistent ID and village association are checked
     * @return whether the living actor belongs to an ongoing raid
     */
    public boolean isRaider(Entity entity) {
        if (entity.getLevel() != level || !entity.isAlive() || entity.isClosed()) return false;
        String villageId = entity.getNbt().getString("RaidVillageUUID");
        if (villageId.isEmpty()) return false;
        Village village;
        try {
            village = villages.get(UUID.fromString(villageId));
        } catch (IllegalArgumentException ignored) {
            return false;
        }
        VillageRaid raid = village == null ? null : village.raid();
        return raid != null && raid.status() == VillageRaid.STATUS_ONGOING && !raid.isFinished()
                && raid.raiders().contains(entity.uniqueIdLong());
    }

    private void configureRaider(Village village, Entity raider) {
        BlockVector3 center = village.center();
        if (raider instanceof EntityIntelligent intelligent) {
            intelligent.getMemoryStorage().put(CoreMemoryTypes.RAID_TARGET,
                    new Vector3(center.x + 0.5, center.y, center.z + 0.5));
        }
        if (raider instanceof EntityIllager illager) illager.setRaiding(true);
        raider.getNbt().putString("RaidVillageUUID", village.uuid().toString());
        raider.setPersistent(true);
    }

    private VillageRaid awardRewards(Village village, VillageRaid raid) {
        playRaidHorn(village);
        for (Player player : level.getPlayers().values()) {
            if (isInsideRaidBounds(village, player)) {
                player.addEffect(Effect.get(EffectType.VILLAGE_HERO)
                        .setDuration(VILLAGE_HERO_DURATION)
                        .setVisible(true));
                player.sendEffectAnimation(EffectType.VILLAGE_HERO);
            }
        }
        celebrate(village);
        return raid.withStatus(VillageRaid.STATUS_VICTORY);
    }

    private boolean hasPlayerInRaidBounds(Village village) {
        for (Player player : level.getPlayers().values()) {
            if (isInsideRaidBounds(village, player)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isInsideRaidBounds(Village village, Player player) {
        BlockVector3 min = village.info().raidBoundsMin();
        BlockVector3 max = village.info().raidBoundsMax();
        return player.getX() > min.x && player.getX() < max.x
                && player.getY() > min.y && player.getY() < max.y
                && player.getZ() > min.z && player.getZ() < max.z;
    }

    private void expireRaiders(VillageRaid raid, boolean celebrating) {
        for (long raiderId : raid.raiders()) {
            Entity raider = level.getEntityByUniqueId(raiderId);
            if (raider == null) {
                continue;
            }
            clearRaider(raider, celebrating);
        }
    }

    private void clearRaider(Entity raider, boolean celebrating) {
        raider.getNbt().remove("RaidVillageUUID");
        if (!raider.hasCustomName()) {
            raider.setPersistent(false);
        }
        if (raider instanceof EntityIllager illager) {
            illager.setRaiding(false);
        }
        if (raider instanceof EntityIntelligent intelligent) {
            intelligent.getMemoryStorage().clear(CoreMemoryTypes.RAID_TARGET);
            intelligent.setMoveTarget(null);
            intelligent.getMemoryStorage().put(CoreMemoryTypes.CELEBRATING, celebrating);
        }
    }

    private void celebrate(Village village) {
        for (VillageDwellers.Dweller dweller : village.dwellers().dwellers()) {
            for (VillageDwellers.Actor actor : dweller.actors()) {
                if (level.getEntityByUniqueId(actor.id()) instanceof EntityVillagerV2 villager) {
                    villager.getMemoryStorage().put(CoreMemoryTypes.CELEBRATING, true);
                }
            }
        }
    }

    private @Nullable Vector3 findSpawnPoint(Village village) {
        Player anchor = findSpawnAnchor(village);
        if (anchor == null) {
            return null;
        }
        BlockVector3 center = village.center();
        double awayX = anchor.getX() - (center.x + 0.5);
        double awayZ = anchor.getZ() - (center.z + 0.5);
        double length = Math.sqrt(awayX * awayX + awayZ * awayZ);
        if (length < 1.0E-4) {
            awayX = 0;
            awayZ = 0;
        } else {
            awayX /= length;
            awayZ /= length;
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int attempt = 0; attempt < RAID_SPAWN_ATTEMPTS; attempt++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = RAID_SPAWN_MIN_RADIUS
                    + random.nextDouble() * (RAID_SPAWN_MAX_RADIUS - RAID_SPAWN_MIN_RADIUS);
            double offsetX = Math.cos(angle) * distance;
            double offsetZ = Math.sin(angle) * distance;
            double dot = offsetX * awayX + offsetZ * awayZ;
            if (dot < 0) {
                offsetX -= awayX * dot * 2;
                offsetZ -= awayZ * dot * 2;
            }

            int x = (int) Math.floor(anchor.getX() + offsetX);
            int z = (int) Math.floor(anchor.getZ() + offsetZ);
            if (level.getChunkIfLoaded(x >> 4, z >> 4) == null) {
                continue;
            }
            int y = level.getHighestBlockAt(x, z) + 1;
            if (level.getBlock(x, y, z, false).isAir()) {
                return new Vector3(x + 0.5, y, z + 0.5);
            }
        }
        return null;
    }

    private @Nullable Player findSpawnAnchor(Village village) {
        BlockVector3 center = village.center();
        Player closest = null;
        double closestDistance = Double.MAX_VALUE;
        for (Player player : level.getPlayers().values()) {
            if (!isInsideRaidBounds(village, player)) {
                continue;
            }
            double distance = player.distanceSquared(new Vector3(center.x + 0.5, center.y, center.z + 0.5));
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = player;
            }
        }
        return closest;
    }

    private List<Long> spawnGroup(Village village, VillageRaid raid) {
        int[] group = RAID_GROUPS[Math.min(raid.groupNumber(), RAID_GROUPS.length - 1)];
        Position spawnPosition = Position.fromObject(raid.spawnPosition(), level);
        List<Long> raiders = new ArrayList<>();
        List<EntityRavager> mounts = new ArrayList<>();
        List<Entity> riders = new ArrayList<>();
        for (int type = 0; type < RAIDER_TYPES.length; type++) {
            for (int spawned = 0; spawned < group[type]; spawned++) {
                Entity raider = Entity.createEntity(RAIDER_TYPES[type], spawnPosition);
                if (raider == null) {
                    continue;
                }

                CreatureSpawnEvent event = new CreatureSpawnEvent(
                        Registries.ENTITY.getEntityNetworkId(RAIDER_TYPES[type]), spawnPosition,
                        new CompoundTag(), CreatureSpawnEvent.SpawnReason.VILLAGE_INVASION);
                level.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    raider.close();
                    continue;
                }

                configureRaider(village, raider);
                raider.spawnToAll();
                raiders.add(raider.uniqueIdLong());
                if (raider instanceof EntityRavager ravager) {
                    mounts.add(ravager);
                } else if (type == RAID_GROUP_RIDERS[Math.min(raid.groupNumber(), RAID_GROUP_RIDERS.length - 1)]) {
                    riders.add(raider);
                }
            }
        }

        for (int i = 0; i < Math.min(mounts.size(), riders.size()); i++) {
            mounts.get(i).mountEntity(riders.get(i));
        }
        return raiders;
    }

    private float totalMaxHealth(List<Long> raiders) {
        float total = 0f;
        for (long raiderId : raiders) {
            Entity raider = level.getEntityByUniqueId(raiderId);
            if (raider != null) {
                total += raider.getMaxHealth();
            }
        }
        return total;
    }

    private void updateBossBars(Village village) {
        VillageRaid raid = village.raid();
        if (raid == null) {
            return;
        }
        Map<UUID, DummyBossBar> bars = raidBossBars.computeIfAbsent(village.uuid(), uuid -> new HashMap<>());
        bars.values().removeIf(bar -> {
            Player player = bar.getPlayer();
            if (!player.isConnected() || player.getLevel() != level || !isInsideRaidBounds(village, player)) {
                player.removeBossBar(bar.getBossBarId());
                return true;
            }
            return false;
        });
        String title = bossBarTitle(raid);
        float health = 0f;
        for (long raiderId : raid.raiders()) {
            Entity raider = level.getEntityByUniqueId(raiderId);
            if (raider != null && !raider.isClosed()) {
                raid.raiderHealth().put(raiderId, raider.isAlive() ? raider.getHealth() : 0f);
            }
            health += raid.raiderHealth().getOrDefault(raiderId, 0f);
        }
        float length = Math.max(1f, raid.bossBarProgress(health) * 100f);
        for (Player player : level.getPlayers().values()) {
            if (!player.isConnected() || !isInsideRaidBounds(village, player)) {
                continue;
            }
            UUID playerUuid = player.getUniqueId();
            DummyBossBar bar = bars.get(playerUuid);
            if (bar == null || bar.getPlayer() != player || player.getDummyBossBar(bar.getBossBarId()) != bar) {
                if (bar != null) {
                    bar.getPlayer().removeBossBar(bar.getBossBarId());
                }
                bar = new DummyBossBar.Builder(player).text(title).length(length).color(BossBarColor.RED).build();
                player.createBossBar(bar);
                bars.put(playerUuid, bar);
            } else {
                bar.setText(title);
                bar.setLength(length);
            }
        }
    }

    /**
     * Restores a loaded raider's village target, or clears its raid state if the saved raid ended.
     * Reads only loaded entities and does not load chunks. Called during entity initialization.
     *
     * @param raider the initialized entity whose saved raid association is being restored
     */
    public void restoreRaider(EntityIntelligent raider) {
        String villageId = raider.getNbt().getString("RaidVillageUUID");
        if (villageId.isEmpty()) {
            return;
        }
        Village village;
        try {
            village = villages.get(UUID.fromString(villageId));
        } catch (IllegalArgumentException ignored) {
            clearRaider(raider, false);
            return;
        }
        VillageRaid raid = village == null ? null : village.raid();
        if (raid != null && raid.state() == VillageRaid.STATE_CELEBRATING
                && raid.status() == VillageRaid.STATUS_LOSS
                && raid.raiders().contains(raider.uniqueIdLong())) {
            clearRaider(raider, true);
            return;
        }
        if (raid == null || raid.isFinished() || raid.state() == VillageRaid.STATE_CELEBRATING
                || !raid.raiders().contains(raider.uniqueIdLong())) {
            clearRaider(raider, false);
            return;
        }
        BlockVector3 center = village.center();
        raider.getMemoryStorage().put(CoreMemoryTypes.RAID_TARGET,
                new Vector3(center.x + 0.5, center.y, center.z + 0.5));
    }

    /**
     * Captures a loaded raider's health before saving or closing it. Does not load chunks.
     * The health map also accepts snapshots from chunk save callbacks.
     *
     * @param raider entity whose current health must survive a chunk unload
     */
    public void rememberRaiderHealth(@NotNull EntityIntelligent raider) {
        String villageId = raider.getNbt().getString("RaidVillageUUID");
        if (villageId.isEmpty()) return;
        Village village;
        try {
            village = villages.get(UUID.fromString(villageId));
        } catch (IllegalArgumentException ignored) {
            return;
        }
        VillageRaid raid = village == null ? null : village.raid();
        if (raid != null && !raid.isFinished() && raid.raiders().contains(raider.uniqueIdLong())) {
            raid.raiderHealth().put(raider.uniqueIdLong(), raider.getHealth());
        }
    }

    private void restoreRaiderTargets(Village village, VillageRaid raid) {
        BlockVector3 center = village.center();
        for (long raiderId : raid.raiders()) {
            if (level.getEntityByUniqueId(raiderId) instanceof EntityIntelligent raider
                    && raider.isAlive() && !raider.isClosed()) {
                village.raiderPositions().put(raiderId, raider.asBlockVector3());
                raider.getNbt().putString("RaidVillageUUID", village.uuid().toString());
                if (raider.getMemoryStorage().isEmpty(CoreMemoryTypes.RAID_TARGET)) {
                    raider.getMemoryStorage().put(CoreMemoryTypes.RAID_TARGET,
                            new Vector3(center.x + 0.5, center.y, center.z + 0.5));
                }
            }
        }
    }

    private void stopRaid(Village village) {
        removeBossBars(village);
        if (village.raid() != null) {
            expireRaiders(village.raid(), false);
            village.setRaid(null);
        }
        village.raiderPositions().clear();
        village.setBellRingTick(0);
        setDwellersHiding(village, false);
    }

    private void setDwellersHiding(Village village, boolean hiding) {
        for (VillageDwellers.Dweller dweller : village.dwellers().dwellers()) {
            for (VillageDwellers.Actor actor : dweller.actors()) {
                if (level.getEntityByUniqueId(actor.id()) instanceof EntityVillagerV2 villager) {
                    villager.getMemoryStorage().put(CoreMemoryTypes.HIDING_FROM_RAID, hiding);
                }
            }
        }
    }

    private static String bossBarTitle(VillageRaid raid) {
        if (raid.status() == VillageRaid.STATUS_LOSS) {
            return "%raid.name - %raid.defeat";
        }
        if (raid.status() == VillageRaid.STATUS_VICTORY) {
            return "%raid.name - %raid.victory";
        }
        return switch (raid.state()) {
            case VillageRaid.STATE_GROUP_IN_PLAY -> "%raid.name - %raid.progress " + raid.raiders().size();
            case VillageRaid.STATE_AWARDING_REWARDS -> "%raid.name - %raid.victory";
            default -> "%raid.name";
        };
    }

    private void removeBossBars(Village village) {
        Map<UUID, DummyBossBar> bars = raidBossBars.remove(village.uuid());
        if (bars != null) {
            for (DummyBossBar bar : bars.values()) {
                bar.getPlayer().removeBossBar(bar.getBossBarId());
            }
        }
    }

    public synchronized void onBlockChange(Block previous, Block current) {
        PoiType previousType = getPoiType(previous);
        PoiType currentType = getPoiType(current);
        if (previousType == null && currentType == null) return;
        BlockVector3 previousPosition = previousType == null ? null : getPoiPosition(previous, previousType);
        BlockVector3 currentPosition = currentType == null ? null : getPoiPosition(current, currentType);
        if (previousType == currentType && previousType != null && previous.getId().equals(current.getId()) && samePosition(previousPosition, currentPosition)) return;
        if (previousType != null) removePoi(previousPosition);
        if (currentType != null) addPoi(currentPosition, currentType);
    }

    public static @Nullable PoiType getPoiType(Block block) {
        if (block instanceof BlockBed) {
            return PoiType.HOME;
        }
        if (block instanceof BlockBell) {
            return PoiType.MEETING;
        }
        return getJobSiteBlockIds().contains(block.getId()) ? PoiType.ACQUIRABLE_JOB_SITE : null;
    }

    private void removePoi(BlockVector3 position) {
        for (Village village : villages.values()) {
            boolean removed = false;
            for (VillagePoiGroup group : village.pois().poi()) {
                if (group.instances().removeIf(poi -> samePosition(poi.position(), position))) {
                    removed = true;
                    break;
                }
            }
            if (removed) {
                village.pois().poi().removeIf(group -> group.villagerId() == -1 && group.instances().isEmpty());
                if (village.houseCount() == 0) {
                    dissolveVillage(village);
                    continue;
                }
                village.setInfo(recalculateBounds(village.pois().poi(), village.info()));
            }
        }
    }

    private void dissolveVillage(Village village) {
        VillageRaid raid = village.raid();
        if (raid != null) {
            if (!raid.isFinished() && raid.status() == VillageRaid.STATUS_ONGOING) village.setRaid(loseRaid(raid));
            return;
        }
        if (!removeVillage(village)) {
            return;
        }
        for (VillageDwellers.Dweller dweller : village.dwellers().dwellers()) {
            for (VillageDwellers.Actor actor : dweller.actors()) {
                if (level.getEntityByUniqueId(actor.id()) instanceof EntityVillagerV2 villager) {
                    villager.leaveVillage(village.uuid());
                }
            }
        }
    }

    private void addPoi(BlockVector3 position, PoiType type) {
        Village village = getVillageAt(position).orElse(null);
        if (village == null || findAt(position) != null) {
            return;
        }
        VillagePoi poi = createPoi(type, position);
        if (poi == null) return;
        List<VillagePoiGroup> groups = village.pois().poi();
        int unownedIndex = -1;
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).villagerId() == -1) {
                unownedIndex = i;
                break;
            }
        }
        if (unownedIndex < 0) {
            groups.add(new VillagePoiGroup(-1, List.of(poi)));
        } else {
            groups.get(unownedIndex).instances().add(poi);
        }
        village.setInfo(recalculateBounds(groups, village.info()));
    }

    private static boolean isInside(VillageInfo info, BlockVector3 position) {
        return isInside(info.boundsMin(), info.boundsMax(), position);
    }

    private static boolean isInside(BlockVector3 min, BlockVector3 max, BlockVector3 position) {
        return position.x >= min.x && position.x <= max.x
                && position.y >= min.y && position.y <= max.y
                && position.z >= min.z && position.z <= max.z;
    }

    public Optional<VillagePoi> findClosest(Predicate<PoiType> type, Vector3 center, int radius,
                                            boolean requireSpace) {
        double radiusSquared = (double) radius * radius;
        return villages.values().stream()
                .flatMap(village -> village.pois().poi().stream())
                .flatMap(group -> group.instances().stream())
                .filter(poi -> type.test(poi.type()) && (!requireSpace || poi.hasSpace()))
                .filter(poi -> distanceSquared(poi.position(), center) <= radiusSquared)
                .min(Comparator.comparingDouble(poi -> distanceSquared(poi.position(), center)));
    }

    public Optional<VillagePoi> findClosestJobSite(Vector3 center, String requiredBlockId) {
        return findClosestJobSite(center, requiredBlockId, null);
    }

    public Optional<VillagePoi> findClosestJobSite(Vector3 center, String requiredBlockId,
                                                    @Nullable UUID villageUuid) {
        return villages.values().stream()
                .filter(village -> villageUuid == null || village.uuid().equals(villageUuid))
                .flatMap(village -> village.pois().poi().stream())
                .flatMap(group -> group.instances().stream())
                .filter(poi -> poi.type() == PoiType.ACQUIRABLE_JOB_SITE && poi.hasSpace())
                .filter(poi -> requiredBlockId == null || requiredBlockId.equals(level.getBlock(
                        poi.position().x, poi.position().y, poi.position().z).getId()))
                .min(Comparator.comparingDouble(poi -> distanceSquared(poi.position(), center)));
    }

    public Optional<VillagePoi> findClosestAvailableHome(UUID villageUuid, Vector3 center) {
        Village village = villages.get(villageUuid);
        if (village == null) {
            return Optional.empty();
        }
        return village.pois().poi().stream()
                .flatMap(group -> group.instances().stream())
                .filter(poi -> poi.type() == PoiType.HOME && poi.hasSpace())
                .min(Comparator.comparingDouble(poi -> distanceSquared(poi.position(), center)));
    }

    public synchronized boolean takeAt(BlockVector3 position) {
        return takeAt(position, -1);
    }

    /** Claims a POI for a villager and creates or expands its village when required. */
    public synchronized boolean takeAt(BlockVector3 position, long villagerId) {
        Block block = level.getBlock(position.x, position.y, position.z, false);
        PoiType type = getPoiType(block);
        if (type == null) return false;
        position = getPoiPosition(block, type);
        VillagePoi existing = findAt(position);
        if (existing != null) {
            if (!existing.hasSpace()) {
                return false;
            }
            return existing.ownerCount() == 0 && villagerId != -1
                    ? claimExistingPoi(position, villagerId)
                    : updateOwnerCount(position, ownerCount -> ownerCount + 1, true);
        }

        Village village = findVillageForClaim(position).orElse(null);
        if (village == null) {
            if (type != PoiType.HOME) {
                return false;
            }
            village = createVillage(getHomeCenter(position));
            if (findAt(position) != null) {
                return villagerId != -1
                        ? claimExistingPoi(position, villagerId)
                        : updateOwnerCount(position, ownerCount -> ownerCount + 1, true);
            }
        }

        VillagePoi claimed = createPoi(type, position);
        if (claimed == null) return false;
        claimed.setOwnerCount(1);
        List<VillagePoiGroup> groups = village.pois().poi();
        int groupIndex = findGroup(groups, villagerId);
        if (groupIndex < 0) {
            groups.add(new VillagePoiGroup(villagerId, List.of(claimed)));
        } else {
            groups.get(groupIndex).set(claimed);
        }
        village.setInfo(expandToInclude(village.info(), position));
        return true;
    }

    public synchronized boolean ensureTicket(BlockVector3 position) {
        VillagePoi poi = findAt(position);
        return poi == null || poi.ownerCount() > 0 || takeAt(position);
    }

    public synchronized boolean ensureTicket(BlockVector3 position, long villagerId) {
        VillagePoi poi = findAt(position);
        return poi == null ? takeAt(position, villagerId)
                : poi.ownerCount() > 0 || takeAt(position, villagerId);
    }

    public synchronized void release(BlockVector3 position) {
        updateOwnerCount(position, ownerCount -> Math.max(0, ownerCount - 1), false);
    }

    private boolean updateOwnerCount(BlockVector3 position, java.util.function.LongUnaryOperator update,
                                     boolean requireSpace) {
        for (Village village : villages.values()) {
            for (VillagePoiGroup group : village.pois().poi()) {
                for (VillagePoi poi : group.instances()) {
                    if (samePosition(poi.position(), position) && (!requireSpace || poi.hasSpace())) {
                        poi.setOwnerCount(update.applyAsLong(poi.ownerCount()));
                        village.setInfo(recalculateBounds(village.pois().poi(), village.info()));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean claimExistingPoi(BlockVector3 position, long villagerId) {
        for (Village village : villages.values()) {
            VillagePoi claimed = null;
            VillagePoiGroup sourceGroup = null;
            for (VillagePoiGroup group : village.pois().poi()) {
                for (VillagePoi poi : group.instances()) {
                    if (claimed == null && samePosition(poi.position(), position) && poi.hasSpace()) {
                        claimed = poi;
                        sourceGroup = group;
                        break;
                    }
                }
            }
            if (claimed == null) {
                continue;
            }
            claimed.setOwnerCount(claimed.ownerCount() + 1);
            sourceGroup.instances().remove(claimed);
            List<VillagePoiGroup> groups = village.pois().poi();
            groups.removeIf(group -> group.villagerId() == -1 && group.instances().isEmpty());
            int groupIndex = findGroup(groups, villagerId);
            if (groupIndex < 0) {
                groups.add(new VillagePoiGroup(villagerId, List.of(claimed)));
            } else {
                groups.get(groupIndex).set(claimed);
            }
            village.setInfo(recalculateBounds(groups, village.info()));
            return true;
        }
        return false;
    }

    private VillagePoi findAt(BlockVector3 position) {
        for (Village village : villages.values()) {
            for (VillagePoiGroup group : village.pois().poi()) {
                for (VillagePoi poi : group.instances()) {
                    if (samePosition(poi.position(), position)) {
                        return poi;
                    }
                }
            }
        }
        return null;
    }

    private Optional<Village> findVillageForClaim(BlockVector3 position) {
        Optional<Village> containing = getVillageAt(position);
        if (containing.isPresent()) {
            return containing;
        }
        return villages.values().stream()
                .filter(village -> isInsideExpansionRange(village.info(), position))
                .min(Comparator.comparingDouble(village -> distanceSquaredToBounds(village.info(), position)));
    }

    private BlockVector3 getHomeCenter(BlockVector3 position) {
        if (level.getBlock(position.x, position.y, position.z, false) instanceof BlockBed bed) {
            if (bed.isHeadPiece()) {
                return position;
            }
            BlockFace face = bed.getBlockFace();
            BlockVector3 head = position.getSide(face);
            if (level.getBlock(head.x, head.y, head.z, false) instanceof BlockBed headPart
                    && headPart.isHeadPiece() && headPart.getBlockFace() == face) {
                return head;
            }
        }
        return position;
    }

    private static int findGroup(List<VillagePoiGroup> groups, long villagerId) {
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).villagerId() == villagerId) {
                return i;
            }
        }
        return -1;
    }

    private static VillageInfo expandToInclude(VillageInfo info, BlockVector3 position) {
        BlockVector3 min = new BlockVector3(Math.min(info.boundsMin().x, position.x),
                Math.min(info.boundsMin().y, position.y), Math.min(info.boundsMin().z, position.z));
        BlockVector3 max = new BlockVector3(Math.max(info.boundsMax().x, position.x),
                Math.max(info.boundsMax().y, position.y), Math.max(info.boundsMax().z, position.z));
        return withBounds(info, min, max);
    }

    private VillageInfo recalculateBounds(List<VillagePoiGroup> groups, VillageInfo fallback) {
        VillagePoi first = null;
        for (VillagePoiGroup group : groups) {
            for (VillagePoi poi : group.instances()) {
                if (poi.ownerCount() > 0) {
                    first = poi;
                    break;
                }
            }
            if (first != null) {
                break;
            }
        }
        if (first == null) {
            return fallback;
        }
        BlockVector3 origin = first.type() == PoiType.HOME ? getHomeCenter(first.position()) : first.position();
        int minX = origin.x - INITIAL_HORIZONTAL_RADIUS;
        int minY = origin.y - INITIAL_VERTICAL_RADIUS;
        int minZ = origin.z - INITIAL_HORIZONTAL_RADIUS;
        int maxX = origin.x + INITIAL_HORIZONTAL_RADIUS;
        int maxY = origin.y + INITIAL_VERTICAL_RADIUS;
        int maxZ = origin.z + INITIAL_HORIZONTAL_RADIUS;
        for (VillagePoiGroup group : groups) {
            for (VillagePoi poi : group.instances()) {
                if (poi.ownerCount() <= 0) {
                    continue;
                }
                BlockVector3 position = poi.position();
                minX = Math.min(minX, position.x);
                minY = Math.min(minY, position.y);
                minZ = Math.min(minZ, position.z);
                maxX = Math.max(maxX, position.x);
                maxY = Math.max(maxY, position.y);
                maxZ = Math.max(maxZ, position.z);
            }
        }
        return withBounds(fallback, new BlockVector3(minX, minY, minZ), new BlockVector3(maxX, maxY, maxZ));
    }

    private static VillageInfo withInitialized(VillageInfo info) {
        return new VillageInfo(info.breedingCooldownTime(), info.golemSpawnCooldownTime(), true,
                info.mergeTick(), info.playerDetectionTick(), info.raidBoundsMin(), info.raidBoundsMax(),
                info.tick(), info.version(), info.boundsMin(), info.boundsMax());
    }

    private static VillageInfo withBounds(VillageInfo info, BlockVector3 min, BlockVector3 max) {
        return new VillageInfo(info.breedingCooldownTime(), info.golemSpawnCooldownTime(), info.initialized(),
                info.mergeTick(), info.playerDetectionTick(), info.raidBoundsMin(), info.raidBoundsMax(),
                info.tick(), info.version(), min, max);
    }

    private static boolean isInsideExpansionRange(VillageInfo info, BlockVector3 position) {
        return position.x >= info.boundsMin().x - HORIZONTAL_EXPANSION_RANGE
                && position.x <= info.boundsMax().x + HORIZONTAL_EXPANSION_RANGE
                && position.y >= info.boundsMin().y - VERTICAL_EXPANSION_RANGE
                && position.y <= info.boundsMax().y + VERTICAL_EXPANSION_RANGE
                && position.z >= info.boundsMin().z - HORIZONTAL_EXPANSION_RANGE
                && position.z <= info.boundsMax().z + HORIZONTAL_EXPANSION_RANGE;
    }

    private static double distanceSquaredToBounds(VillageInfo info, BlockVector3 position) {
        int dx = Math.max(0, Math.max(info.boundsMin().x - position.x, position.x - info.boundsMax().x));
        int dy = Math.max(0, Math.max(info.boundsMin().y - position.y, position.y - info.boundsMax().y));
        int dz = Math.max(0, Math.max(info.boundsMin().z - position.z, position.z - info.boundsMax().z));
        return (double) dx * dx + (double) dy * dy + (double) dz * dz;
    }

    private static boolean samePosition(BlockVector3 first, BlockVector3 second) {
        return first.x == second.x && first.y == second.y && first.z == second.z;
    }

    private static double distanceSquared(BlockVector3 position, Vector3 center) {
        double x = position.x - center.x;
        double y = position.y - center.y;
        double z = position.z - center.z;
        return x * x + y * y + z * z;
    }
}
