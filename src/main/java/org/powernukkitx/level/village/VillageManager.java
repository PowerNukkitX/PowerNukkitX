package org.powernukkitx.level.village;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockBed;
import org.powernukkitx.block.BlockBell;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.data.profession.Profession;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.entity.item.EntityFireworksRocket;
import org.powernukkitx.entity.passive.EntityVillagerV2;
import org.powernukkitx.event.entity.CreatureSpawnEvent;
import org.powernukkitx.level.Level;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.ItemHelper;

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

    public static final int RAID_PREPARATION_TIME = 600;
    public static final int GROUP_COMPLETE_DELAY = 100;
    public static final int ALLOWED_SPAWN_FAILURES = 3;
    public static final int RAID_TRIGGER_HORIZONTAL_RADIUS = 32;
    public static final int RAID_TRIGGER_VERTICAL_RADIUS = 11;
    public static final int RAID_BOSS_BAR_HORIZONTAL_RADIUS = 64;
    public static final int RAID_BOSS_BAR_VERTICAL_RADIUS = 44;
    public static final int RAID_SPAWN_MIN_RADIUS = 24;
    public static final int RAID_SPAWN_MAX_RADIUS = 64;
    public static final int VILLAGE_HERO_DURATION = 40 * 60 * 20;
    public static final int CELEBRATION_DURATION = 30 * 20;
    public static final int CELEBRATION_FIREWORK_INTERVAL = 90;

    private static final String[] RAIDER_TYPES = {
            EntityID.PILLAGER, EntityID.VINDICATOR, EntityID.RAVAGER, EntityID.WITCH, EntityID.EVOCATION_ILLAGER
    };

    /**
     * How many of each {@link #RAIDER_TYPES} every group is made of, the first row being the first
     * group. Easy stops after three groups, normal after five, hard runs the seven.
     */
    private static final int[][] RAID_GROUPS = {
            {4, 0, 0, 0, 0},
            {3, 2, 0, 0, 0},
            {3, 4, 1, 0, 0},
            {3, 2, 0, 3, 1},
            {5, 6, 1, 0, 1},
            {0, 0, 2, 1, 0},
            {0, 0, 1, 1, 2}
    };

    private final Level level;
    private final ConcurrentHashMap<UUID, Village> villages = new ConcurrentHashMap<>();
    private final Map<UUID, Map<UUID, Long>> raidBossBars = new HashMap<>();
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
        BlockVector3 min = center.add(-INITIAL_HORIZONTAL_RADIUS, -INITIAL_VERTICAL_RADIUS,
                -INITIAL_HORIZONTAL_RADIUS);
        BlockVector3 max = center.add(INITIAL_HORIZONTAL_RADIUS, INITIAL_VERTICAL_RADIUS,
                INITIAL_HORIZONTAL_RADIUS);
        long tick = level.getCurrentTick();
        VillageInfo info = new VillageInfo(0, 0, true, tick, tick, min, max, tick, (byte) 1, min, max);
        Village village = new Village(UUID.randomUUID(), discoverDwellers(min, max, tick), info,
                new VillagePlayers(new ListTag<Tag>()), discoverPois(min, max), null);
        villages.put(village.uuid(), village);
        return village;
    }

    private VillageDwellers discoverDwellers(BlockVector3 min, BlockVector3 max, long tick) {
        List<VillageDwellers.Actor> actors = new ArrayList<>();
        for (int chunkX = min.x >> 4; chunkX <= max.x >> 4; chunkX++) {
            for (int chunkZ = min.z >> 4; chunkZ <= max.z >> 4; chunkZ++) {
                IChunk chunk = level.getChunkIfLoaded(chunkX, chunkZ);
                if (chunk == null) {
                    continue;
                }
                for (var entity : chunk.getEntities().values()) {
                    if (entity instanceof EntityVillagerV2 villager) {
                        BlockVector3 pos = villager.asBlockVector3();
                        if (isInside(min, max, pos)) {
                            actors.add(new VillageDwellers.Actor(villager.runtimeId(), pos, tick, null));
                        }
                    }
                }
            }
        }
        return actors.isEmpty()
                ? new VillageDwellers(List.of())
                : new VillageDwellers(List.of(new VillageDwellers.Dweller(actors)));
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
                            if (block instanceof BlockBed bed && !bed.isHeadPiece()) {
                                type = PoiType.HOME;
                            } else if (block instanceof BlockBell) {
                                type = PoiType.MEETING;
                            } else if (jobSiteIds.contains(block.getId())) {
                                type = PoiType.ACQUIRABLE_JOB_SITE;
                            }
                            if (type != null) {
                                pois.add(new VillagePoi(type, new BlockVector3(x, y, z)));
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
        if (village == null || isDweller(actor.id())) {
            return;
        }
        List<VillageDwellers.Dweller> dwellers = village.dwellers().dwellers();
        if (dwellers.isEmpty()) {
            dwellers.add(new VillageDwellers.Dweller(List.of(actor)));
        } else {
            dwellers.getFirst().actors().add(actor);
        }
    }

    public void addVillage(Village village) {
        villages.put(village.uuid(), village);
    }

    public boolean removeVillage(Village village) {
        return villages.remove(village.uuid(), village);
    }

    public Village removeVillage(UUID uuid) {
        return villages.remove(uuid);
    }

    public void clear() {
        villages.clear();
    }

    public void load(Collection<Village> villages) {
        this.villages.clear();
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
                    tryStartRaid(village, currentTick);
                }
                continue;
            }
            village.setRaid(tickRaid(village, raid));
            setDwellersHiding(village, village.raid() != null);
            updateBossBars(village);
        }
    }

    private void tryStartRaid(Village village, long currentTick) {
        if (level.getServer().getDifficulty() == 0 || !village.isValid()) {
            return;
        }
        BlockVector3 center = village.center();
        for (Player player : level.getPlayers().values()) {
            if (player.getEffect(EffectType.BAD_OMEN) == null || !isInsideRaidTrigger(center, player)) {
                continue;
            }
            player.removeEffect(EffectType.BAD_OMEN);
            village.setRaid(VillageRaid.starting(currentTick, groupCount(),
                    new Vector3(center.x + 0.5, center.y, center.z + 0.5)));
            return;
        }
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
            return null;
        }
        if (!village.isValid()) {
            return raid.withStatus(VillageRaid.STATUS_LOSS);
        }
        return switch (raid.state()) {
            case VillageRaid.STATE_PREPARATION -> tickPreparation(raid);
            case VillageRaid.STATE_PICKING_SPAWN_POINT -> tickPickingSpawnPoint(village, raid);
            case VillageRaid.STATE_SPAWNING_GROUP -> tickSpawningGroup(village, raid);
            case VillageRaid.STATE_GROUP_IN_PLAY -> tickGroupInPlay(raid);
            case VillageRaid.STATE_AWARDING_REWARDS -> awardRewards(village, raid);
            default -> raid.withStatus(VillageRaid.STATUS_STOPPED);
        };
    }

    private VillageRaid tickPreparation(VillageRaid raid) {
        long ticks = raid.ticks() + 1;
        return ticks >= RAID_PREPARATION_TIME
                ? raid.withState(VillageRaid.STATE_PICKING_SPAWN_POINT)
                : raid.withTicks(ticks);
    }

    private VillageRaid tickPickingSpawnPoint(Village village, VillageRaid raid) {
        Vector3 spawnPoint = findSpawnPoint(village);
        if (spawnPoint != null) {
            return raid.withSpawnPoint(spawnPoint, 0).withState(VillageRaid.STATE_SPAWNING_GROUP);
        }
        int spawnFails = raid.spawnFails() + 1;
        return spawnFails > ALLOWED_SPAWN_FAILURES
                ? raid.withStatus(VillageRaid.STATUS_STOPPED)
                : raid.withSpawnPoint(raid.spawnPosition(), spawnFails);
    }

    private VillageRaid tickSpawningGroup(Village village, VillageRaid raid) {
        List<Long> raiders = spawnGroup(village, raid);
        if (!raiders.isEmpty()) {
            return raid.withGroup(raid.groupNumber() + 1, raiders, totalMaxHealth(raiders));
        }
        int spawnFails = raid.spawnFails() + 1;
        return spawnFails > ALLOWED_SPAWN_FAILURES
                ? raid.withStatus(VillageRaid.STATUS_STOPPED)
                : raid.withSpawnPoint(raid.spawnPosition(), spawnFails)
                        .withState(VillageRaid.STATE_PICKING_SPAWN_POINT);
    }

    private VillageRaid tickGroupInPlay(VillageRaid raid) {
        List<Long> alive = new ArrayList<>();
        for (long raiderId : raid.raiders()) {
            Entity raider = level.getEntity(raiderId);
            if (raider != null && !raider.isClosed() && raider.isAlive()) {
                alive.add(raiderId);
            }
        }
        if (!alive.isEmpty()) {
            return raid.withRaiders(alive);
        }
        long ticks = raid.ticks() + 1;
        if (ticks < GROUP_COMPLETE_DELAY) {
            return raid.withRaiders(alive).withTicks(ticks);
        }
        return raid.withRaiders(alive).withState(raid.groupNumber() < raid.numberOfGroups()
                ? VillageRaid.STATE_PICKING_SPAWN_POINT
                : VillageRaid.STATE_AWARDING_REWARDS);
    }

    private VillageRaid awardRewards(Village village, VillageRaid raid) {
        if (raid.ticks() == 0) {
            BlockVector3 center = village.center();
            for (Player player : level.getPlayers().values()) {
                if (isInsideBossBarRange(center, player)) {
                    player.addEffect(Effect.get(EffectType.VILLAGE_HERO)
                            .setDuration(VILLAGE_HERO_DURATION)
                            .setVisible(true));
                }
            }
        }

        celebrate(village, raid.ticks());
        long ticks = raid.ticks() + 1;
        return ticks < CELEBRATION_DURATION
                ? raid.withTicks(ticks)
                : raid.withStatus(VillageRaid.STATUS_VICTORY);
    }

    private void celebrate(Village village, long ticks) {
        for (VillageDwellers.Dweller dweller : village.dwellers().dwellers()) {
            for (VillageDwellers.Actor actor : dweller.actors()) {
                if (!(level.getEntity(actor.id()) instanceof EntityVillagerV2 villager)
                        || (ticks + villager.runtimeId()) % CELEBRATION_FIREWORK_INTERVAL != 0) {
                    continue;
                }
                CompoundTag nbt = Entity.getDefaultNBT(villager.add(0, villager.getEyeHeight(), 0));
                nbt.putCompound("FireworkItem", ItemHelper.write(Item.get(ItemID.FIREWORK_ROCKET)));
                new EntityFireworksRocket(villager.getChunk(), nbt).spawnToAll();
            }
        }
    }

    private @Nullable Vector3 findSpawnPoint(Village village) {
        BlockVector3 center = village.center();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int attempt = 0; attempt < 16; attempt++) {
            double angle = random.nextDouble() * Math.PI * 2;
            int distance = random.nextInt(RAID_SPAWN_MIN_RADIUS, RAID_SPAWN_MAX_RADIUS + 1);
            int x = center.x + (int) (Math.cos(angle) * distance);
            int z = center.z + (int) (Math.sin(angle) * distance);
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

    private List<Long> spawnGroup(Village village, VillageRaid raid) {
        int[] group = RAID_GROUPS[Math.min(raid.groupNumber(), RAID_GROUPS.length - 1)];
        Position spawnPosition = Position.fromObject(raid.spawnPosition(), level);
        BlockVector3 center = village.center();
        Vector3 target = new Vector3(center.x + 0.5, center.y, center.z + 0.5);
        List<Long> raiders = new ArrayList<>();
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

                if (raider instanceof EntityIntelligent intelligent) {
                    intelligent.getMemoryStorage().put(CoreMemoryTypes.RAID_TARGET, target);
                }
                raider.setPersistent(true);
                raider.spawnToAll();
                raiders.add(raider.runtimeId());
            }
        }
        return raiders;
    }

    private float totalMaxHealth(List<Long> raiders) {
        float total = 0f;
        for (long raiderId : raiders) {
            Entity raider = level.getEntity(raiderId);
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
        Map<UUID, Long> bars = raidBossBars.computeIfAbsent(village.uuid(), uuid -> new HashMap<>());
        BlockVector3 center = village.center();
        String title = bossBarTitle(raid);
        int length = (int) (raid.bossBarProgress() * 100);
        for (Player player : level.getPlayers().values()) {
            UUID playerUuid = player.getUniqueId();
            if (!isInsideBossBarRange(center, player)) {
                Long removed = bars.remove(playerUuid);
                if (removed != null) {
                    player.removeBossBar(removed);
                }
                continue;
            }
            Long bossBarId = bars.get(playerUuid);
            if (bossBarId == null) {
                bars.put(playerUuid, player.createBossBar(title, length));
            } else {
                player.updateBossBar(title, length, bossBarId);
            }
        }
    }

    private void setDwellersHiding(Village village, boolean hiding) {
        for (VillageDwellers.Dweller dweller : village.dwellers().dwellers()) {
            for (VillageDwellers.Actor actor : dweller.actors()) {
                if (level.getEntity(actor.id()) instanceof EntityVillagerV2 villager) {
                    villager.getMemoryStorage().put(CoreMemoryTypes.HIDING_FROM_RAID, hiding);
                }
            }
        }
    }

    private static String bossBarTitle(VillageRaid raid) {
        return switch (raid.state()) {
            case VillageRaid.STATE_GROUP_IN_PLAY -> "%raid.progress " + raid.raiders().size();
            case VillageRaid.STATE_AWARDING_REWARDS -> "%raid.victory";
            default -> "%raid.name";
        };
    }

    private void removeBossBars(Village village) {
        Map<UUID, Long> bars = raidBossBars.remove(village.uuid());
        if (bars == null) {
            return;
        }
        for (Player player : level.getPlayers().values()) {
            Long bossBarId = bars.get(player.getUniqueId());
            if (bossBarId != null) {
                player.removeBossBar(bossBarId);
            }
        }
    }

    private static boolean isInsideRaidTrigger(BlockVector3 center, Player player) {
        return isInsideRange(center, player, RAID_TRIGGER_HORIZONTAL_RADIUS, RAID_TRIGGER_VERTICAL_RADIUS);
    }

    private static boolean isInsideBossBarRange(BlockVector3 center, Player player) {
        return isInsideRange(center, player, RAID_BOSS_BAR_HORIZONTAL_RADIUS, RAID_BOSS_BAR_VERTICAL_RADIUS);
    }

    private static boolean isInsideRange(BlockVector3 center, Player player, int horizontal, int vertical) {
        return Math.abs(player.getX() - center.x) <= horizontal
                && Math.abs(player.getY() - center.y) <= vertical
                && Math.abs(player.getZ() - center.z) <= horizontal;
    }

    public synchronized void onBlockChange(Block previous, Block current) {
        PoiType previousType = getPoiType(previous);
        PoiType currentType = getPoiType(current);
        if (previousType == null && currentType == null) {
            return;
        }
        BlockVector3 position = current.asBlockVector3();
        if (previousType != null) {
            removePoi(position);
        }
        if (currentType != null) {
            addPoi(position, currentType);
        }
    }

    public static @Nullable PoiType getPoiType(Block block) {
        if (block instanceof BlockBed bed) {
            return bed.isHeadPiece() ? null : PoiType.HOME;
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
                village.pois().poi().removeIf(group -> group.instances().isEmpty());
                if (village.houseCount() == 0) {
                    dissolveVillage(village);
                    continue;
                }
                village.setInfo(recalculateBounds(village.pois().poi(), village.info()));
            }
        }
    }

    private void dissolveVillage(Village village) {
        if (!villages.remove(village.uuid(), village)) {
            return;
        }
        for (VillageDwellers.Dweller dweller : village.dwellers().dwellers()) {
            for (VillageDwellers.Actor actor : dweller.actors()) {
                if (level.getEntity(actor.id()) instanceof EntityVillagerV2 villager) {
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
        List<VillagePoiGroup> groups = village.pois().poi();
        int unownedIndex = -1;
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).villagerId() == -1) {
                unownedIndex = i;
                break;
            }
        }
        if (unownedIndex < 0) {
            groups.add(new VillagePoiGroup(-1, List.of(new VillagePoi(type, position))));
        } else {
            groups.get(unownedIndex).instances().add(new VillagePoi(type, position));
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
        VillagePoi existing = findAt(position);
        if (existing != null) {
            if (!existing.hasSpace()) {
                return false;
            }
            return existing.ownerCount() == 0 && villagerId >= 0
                    ? claimExistingPoi(position, villagerId)
                    : updateOwnerCount(position, ownerCount -> ownerCount + 1, true);
        }

        PoiType type = getPoiType(level.getBlock(position.x, position.y, position.z, false));
        if (type == null) {
            return false;
        }
        Village village = findVillageForClaim(position).orElse(null);
        if (village == null) {
            if (type != PoiType.HOME) {
                return false;
            }
            village = createVillage(getHomeCenter(position));
            if (findAt(position) != null) {
                return villagerId >= 0
                        ? claimExistingPoi(position, villagerId)
                        : updateOwnerCount(position, ownerCount -> ownerCount + 1, true);
            }
        }

        VillagePoi claimed = new VillagePoi(type, position);
        claimed.setOwnerCount(1);
        List<VillagePoiGroup> groups = village.pois().poi();
        int groupIndex = findGroup(groups, villagerId);
        if (groupIndex < 0) {
            groups.add(new VillagePoiGroup(villagerId, List.of(claimed)));
        } else {
            groups.get(groupIndex).instances().add(claimed);
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
            groups.removeIf(group -> group.instances().isEmpty());
            int groupIndex = findGroup(groups, villagerId);
            if (groupIndex < 0) {
                groups.add(new VillagePoiGroup(villagerId, List.of(claimed)));
            } else {
                groups.get(groupIndex).instances().add(claimed);
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

    private static VillageInfo withBounds(VillageInfo info, BlockVector3 min, BlockVector3 max) {
        return new VillageInfo(info.breedingCooldownTime(), info.golemSpawnCooldownTime(), info.initialized(),
                info.mergeTick(), info.playerDetectionTick(), min, max, info.tick(), info.version(), min, max);
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
