package org.powernukkitx.level.village;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockBed;
import org.powernukkitx.block.BlockBell;
import org.powernukkitx.entity.data.profession.Profession;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import javax.annotation.Nullable;

public final class VillageManager {
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

    public synchronized void addDweller(UUID villageUuid, VillageDwellers.Actor actor) {
        Village village = villages.get(villageUuid);
        if (village == null || isDweller(actor.id())) {
            return;
        }
        List<VillageDwellers.Dweller> dwellers = new ArrayList<>(village.dwellers().dwellers());
        if (dwellers.isEmpty()) {
            dwellers.add(new VillageDwellers.Dweller(List.of(actor)));
        } else {
            VillageDwellers.Dweller first = dwellers.getFirst();
            List<VillageDwellers.Actor> actors = new ArrayList<>(first.actors());
            actors.add(actor);
            dwellers.set(0, new VillageDwellers.Dweller(actors));
        }
        villages.put(villageUuid, new Village(village.uuid(), new VillageDwellers(dwellers), village.info(),
                village.players(), village.pois(), village.raid()));
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
        for (Village village : List.copyOf(villages.values())) {
            List<VillagePoiGroup> groups = village.pois().poi().stream()
                    .map(group -> new VillagePoiGroup(group.villagerId(), group.instances().stream()
                            .filter(poi -> !samePosition(poi.position(), position)).toList()))
                    .filter(group -> !group.instances().isEmpty())
                    .toList();
            if (groups.stream().mapToInt(group -> group.instances().size()).sum()
                    != village.pois().poi().stream().mapToInt(group -> group.instances().size()).sum()) {
                replacePois(village, groups);
            }
        }
    }

    private void addPoi(BlockVector3 position, PoiType type) {
        Village village = getVillageAt(position).orElse(null);
        if (village == null || findAt(position) != null) {
            return;
        }
        List<VillagePoiGroup> groups = new ArrayList<>(village.pois().poi());
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
            VillagePoiGroup group = groups.get(unownedIndex);
            List<VillagePoi> instances = new ArrayList<>(group.instances());
            instances.add(new VillagePoi(type, position));
            groups.set(unownedIndex, new VillagePoiGroup(-1, instances));
        }
        replacePois(village, groups);
    }

    private void replacePois(Village village, List<VillagePoiGroup> groups) {
        villages.put(village.uuid(), new Village(village.uuid(), village.dwellers(), village.info(),
                village.players(), new VillagePois(groups), village.raid()));
    }

    private static boolean isInside(VillageInfo info, BlockVector3 position) {
        return position.x >= info.boundsMin().x && position.x <= info.boundsMax().x
                && position.y >= info.boundsMin().y && position.y <= info.boundsMax().y
                && position.z >= info.boundsMin().z && position.z <= info.boundsMax().z;
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
        return villages.values().stream()
                .flatMap(village -> village.pois().poi().stream())
                .flatMap(group -> group.instances().stream())
                .filter(poi -> poi.type() == PoiType.ACQUIRABLE_JOB_SITE && poi.hasSpace())
                .filter(poi -> requiredBlockId == null || requiredBlockId.equals(level.getBlock(
                        poi.position().x, poi.position().y, poi.position().z).getId()))
                .min(Comparator.comparingDouble(poi -> distanceSquared(poi.position(), center)));
    }

    public synchronized boolean takeAt(BlockVector3 position) {
        VillagePoi poi = findAt(position);
        return poi == null || poi.hasSpace() && updateOwnerCount(position, ownerCount -> ownerCount + 1, true);
    }

    public synchronized boolean ensureTicket(BlockVector3 position) {
        VillagePoi poi = findAt(position);
        return poi == null || poi.ownerCount() > 0 || takeAt(position);
    }

    public synchronized void release(BlockVector3 position) {
        updateOwnerCount(position, ownerCount -> Math.max(0, ownerCount - 1), false);
    }

    private boolean updateOwnerCount(BlockVector3 position, java.util.function.LongUnaryOperator update,
                                     boolean requireSpace) {
        for (Village village : villages.values()) {
            List<VillagePoiGroup> groups = new ArrayList<>();
            boolean changed = false;
            for (VillagePoiGroup group : village.pois().poi()) {
                List<VillagePoi> instances = new ArrayList<>();
                for (VillagePoi poi : group.instances()) {
                    if (samePosition(poi.position(), position) && (!requireSpace || poi.hasSpace())) {
                        poi = poi.withOwnerCount(update.applyAsLong(poi.ownerCount()));
                        changed = true;
                    }
                    instances.add(poi);
                }
                groups.add(new VillagePoiGroup(group.villagerId(), instances));
            }
            if (changed) {
                villages.put(village.uuid(), new Village(village.uuid(), village.dwellers(), village.info(),
                        village.players(), new VillagePois(groups), village.raid()));
                return true;
            }
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
