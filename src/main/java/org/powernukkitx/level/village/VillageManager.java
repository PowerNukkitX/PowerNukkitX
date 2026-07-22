package org.powernukkitx.level.village;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockBed;
import org.powernukkitx.block.BlockBell;
import org.powernukkitx.entity.data.profession.Profession;
import org.powernukkitx.entity.passive.EntityVillagerV2;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import javax.annotation.Nullable;

public final class VillageManager {
    public static final int INITIAL_HORIZONTAL_RADIUS = 32;
    public static final int INITIAL_VERTICAL_RADIUS = 12;
    public static final int HORIZONTAL_EXPANSION_RANGE = 32;
    public static final int VERTICAL_EXPANSION_RANGE = 52;

    private final Level level;
    private final ConcurrentHashMap<UUID, Village> villages = new ConcurrentHashMap<>();

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
        return villages.values().stream()
                .flatMap(village -> village.dwellers().dwellers().stream())
                .flatMap(dweller -> dweller.actors().stream())
                .anyMatch(actor -> actor.id() == entityId);
    }

    public Optional<Village> getVillageForDweller(long entityId) {
        return villages.values().stream()
                .filter(village -> village.dwellers().dwellers().stream()
                        .flatMap(dweller -> dweller.actors().stream())
                        .anyMatch(actor -> actor.id() == entityId))
                .findFirst();
    }

    public Optional<Village> getVillageAt(BlockVector3 position) {
        return villages.values().stream().filter(village -> isInside(village.info(), position)).findFirst();
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
                chunk.getEntities().values().stream()
                        .filter(EntityVillagerV2.class::isInstance)
                        .map(EntityVillagerV2.class::cast)
                        .filter(villager -> isInside(min, max, villager.asBlockVector3()))
                        .map(villager -> new VillageDwellers.Actor(villager.getId(), villager.asBlockVector3(),
                                tick, null))
                        .forEach(actors::add);
            }
        }
        return actors.isEmpty()
                ? new VillageDwellers(List.of())
                : new VillageDwellers(List.of(new VillageDwellers.Dweller(actors)));
    }

    private VillagePois discoverPois(BlockVector3 min, BlockVector3 max) {
        List<VillagePoi> pois = new ArrayList<>();
        var jobSiteIds = new HashSet<String>();
        Profession.getProfessions().values().forEach(profession -> jobSiteIds.add(profession.getBlockID()));
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

    public synchronized void onBlockChange(Block previous, Block current) {
        PoiType previousType = getPoiType(previous);
        PoiType currentType = getPoiType(current);
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
        return Profession.getProfessions().values().stream()
                .anyMatch(profession -> profession.getBlockID().equals(block.getId()))
                ? PoiType.ACQUIRABLE_JOB_SITE : null;
    }

    private void removePoi(BlockVector3 position) {
        for (Village village : villages.values()) {
            boolean removed = village.pois().poi().stream()
                    .anyMatch(group -> group.instances().removeIf(poi -> samePosition(poi.position(), position)));
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
        village.dwellers().dwellers().stream()
                .flatMap(dweller -> dweller.actors().stream())
                .map(actor -> level.getEntity(actor.id()))
                .filter(EntityVillagerV2.class::isInstance)
                .map(EntityVillagerV2.class::cast)
                .forEach(villager -> villager.leaveVillage(village.uuid()));
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

        PoiType type = getPoiType(level.getBlock(position.x, position.y, position.z));
        if (type == null) {
            return false;
        }
        Village village = findVillageForClaim(position, type).orElse(null);
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
        return villages.values().stream()
                .flatMap(village -> village.pois().poi().stream())
                .flatMap(group -> group.instances().stream())
                .filter(poi -> samePosition(poi.position(), position))
                .findFirst().orElse(null);
    }

    private Optional<Village> findVillageForClaim(BlockVector3 position, PoiType type) {
        Optional<Village> containing = getVillageAt(position);
        if (containing.isPresent()) {
            return containing;
        }
        return villages.values().stream()
                .filter(village -> isInsideExpansionRange(village.info(), position))
                .min(Comparator.comparingDouble(village -> distanceSquaredToBounds(village.info(), position)));
    }

    private BlockVector3 getHomeCenter(BlockVector3 position) {
        Block block = level.getBlock(position.x, position.y, position.z);
        if (block instanceof BlockBed bed) {
            BlockBed head = bed.getHeadPart();
            if (head != null) {
                return head.asBlockVector3();
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
        List<VillagePoi> claimed = groups.stream().flatMap(group -> group.instances().stream())
                .filter(poi -> poi.ownerCount() > 0).toList();
        if (claimed.isEmpty()) {
            return fallback;
        }
        VillagePoi first = claimed.getFirst();
        BlockVector3 origin = first.type() == PoiType.HOME ? getHomeCenter(first.position()) : first.position();
        BlockVector3 min = origin.add(-INITIAL_HORIZONTAL_RADIUS, -INITIAL_VERTICAL_RADIUS,
                -INITIAL_HORIZONTAL_RADIUS);
        BlockVector3 max = origin.add(INITIAL_HORIZONTAL_RADIUS, INITIAL_VERTICAL_RADIUS,
                INITIAL_HORIZONTAL_RADIUS);
        for (VillagePoi poi : claimed) {
            BlockVector3 position = poi.position();
            min = new BlockVector3(Math.min(min.x, position.x), Math.min(min.y, position.y),
                    Math.min(min.z, position.z));
            max = new BlockVector3(Math.max(max.x, position.x), Math.max(max.y, position.y),
                    Math.max(max.z, position.z));
        }
        return withBounds(fallback, min, max);
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
