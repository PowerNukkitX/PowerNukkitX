package org.powernukkitx.level.structure;

import com.google.common.base.Preconditions;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector3;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Per-chunk AABBVolumes data stored under LevelDB tag {@code 0x77}.
 *
 * @author Curse
 */
public final class AabbVolumes {
    private static final AabbVolumes EMPTY = new AabbVolumes(List.of(), List.of(), List.of(), List.of());

    private final List<StructureType> structureTypes;
    private final List<BoundingBox> boundingBoxes;
    private final List<DynamicSpawnArea> dynamicSpawnAreas;
    private final List<StaticSpawnArea> staticSpawnAreas;

    private final Map<Integer, StructureType> structureTypesById;
    private final Map<Integer, BoundingBox> boundingBoxesById;
    private final Map<Integer, Integer> structureKeys;
    private final Set<Integer> dynamicSpawnAreaIds;
    private final Set<Integer> staticSpawnAreaIds;
    private final Set<Integer> fullBoundingBoxIds;

    private final List<Integer> boundingBoxOrder;
    private final List<Integer> structureKeyOrder;
    private final List<Integer> dynamicSpawnAreaOrder;
    private final List<Integer> staticSpawnAreaOrder;
    private final List<Integer> fullBoundingBoxOrder;

    /**
     * Creates an immutable AABBVolumes snapshot.
     */
    public AabbVolumes(List<StructureType> structureTypes, List<BoundingBox> boundingBoxes, List<DynamicSpawnArea> dynamicSpawnAreas, List<StaticSpawnArea> staticSpawnAreas) {
        this.structureTypes = List.copyOf(Preconditions.checkNotNull(structureTypes, "structureTypes"));
        this.boundingBoxes = List.copyOf(Preconditions.checkNotNull(boundingBoxes, "boundingBoxes"));
        this.dynamicSpawnAreas = List.copyOf(Preconditions.checkNotNull(dynamicSpawnAreas, "dynamicSpawnAreas"));
        this.staticSpawnAreas = List.copyOf(Preconditions.checkNotNull(staticSpawnAreas, "staticSpawnAreas"));

        LinkedHashMap<Integer, StructureType> structureTypeIndex = new LinkedHashMap<>();
        for (StructureType structureType : this.structureTypes) {
            structureTypeIndex.put(structureType.id(), structureType);
        }
        this.structureTypesById = Map.copyOf(structureTypeIndex);

        LinkedHashMap<Integer, BoundingBox> boundingBoxIndex = new LinkedHashMap<>();
        for (BoundingBox boundingBox : this.boundingBoxes) {
            boundingBoxIndex.put(boundingBox.id(), boundingBox);
        }
        this.boundingBoxesById = Map.copyOf(boundingBoxIndex);
        this.boundingBoxOrder = List.copyOf(boundingBoxIndex.keySet());

        LinkedHashMap<Integer, Integer> structureKeys = new LinkedHashMap<>();
        LinkedHashSet<Integer> dynamicSpawnAreaIds = new LinkedHashSet<>();
        LinkedHashSet<Integer> staticSpawnAreaIds = new LinkedHashSet<>();
        LinkedHashSet<Integer> fullBoundingBoxIds = new LinkedHashSet<>();

        for (DynamicSpawnArea spawnArea : this.dynamicSpawnAreas) {
            dynamicSpawnAreaIds.add(spawnArea.boundingBoxId());
            structureKeys.put(spawnArea.boundingBoxId(), spawnArea.structureId());
            if (spawnArea.fullBoundingBox()) {
                fullBoundingBoxIds.add(spawnArea.boundingBoxId());
            }
        }

        for (StaticSpawnArea spawnArea : this.staticSpawnAreas) {
            staticSpawnAreaIds.add(spawnArea.boundingBoxId());
            structureKeys.put(spawnArea.boundingBoxId(), spawnArea.structureId());
            if (spawnArea.fullBoundingBox()) {
                fullBoundingBoxIds.add(spawnArea.boundingBoxId());
            }
        }

        this.structureKeys = Map.copyOf(structureKeys);
        this.dynamicSpawnAreaIds = Set.copyOf(dynamicSpawnAreaIds);
        this.staticSpawnAreaIds = Set.copyOf(staticSpawnAreaIds);
        this.fullBoundingBoxIds = Set.copyOf(fullBoundingBoxIds);

        this.structureKeyOrder = List.copyOf(structureKeys.keySet());
        this.dynamicSpawnAreaOrder = List.copyOf(dynamicSpawnAreaIds);
        this.staticSpawnAreaOrder = List.copyOf(staticSpawnAreaIds);
        this.fullBoundingBoxOrder = List.copyOf(fullBoundingBoxIds);
    }

    /**
     * Returns an empty AABBVolumes snapshot.
     */
    public static AabbVolumes empty() {
        return EMPTY;
    }

    /**
     * Returns the persisted structure-type table.
     */
    public List<StructureType> getStructureTypes() {
        return structureTypes;
    }

    /**
     * Returns the persisted chunk bounding boxes.
     */
    public List<BoundingBox> getBoundingBoxes() {
        return boundingBoxes;
    }

    /**
     * Returns dynamic spawn-area references.
     */
    public List<DynamicSpawnArea> getDynamicSpawnAreas() {
        return dynamicSpawnAreas;
    }

    /**
     * Returns static spawn-area references.
     */
    public List<StaticSpawnArea> getStaticSpawnAreas() {
        return staticSpawnAreas;
    }

    /**
     * Matches the persistence predicate: no 0x77 value is written when no bounding boxes exist.
     */
    public boolean isEmpty() {
        return boundingBoxes.size() == 0;
    }

    /**
     * Returns structure types whose piece bounding box contains the supplied position.
     */
    public List<String> getStructureTypes(Vector3 position) {
        Preconditions.checkNotNull(position, "position");

        int x = position.getFloorX();
        int y = position.getFloorY();
        int z = position.getFloorZ();

        List<Integer> driver = structureKeyOrder.size() < boundingBoxOrder.size() ? structureKeyOrder : boundingBoxOrder;
        List<String> result = new ArrayList<>();

        for (int i = driver.size() - 1; i >= 0; i--) {
            int boundingBoxId = driver.get(i);

            BoundingBox boundingBox = boundingBoxesById.get(boundingBoxId);
            Integer structureId = structureKeys.get(boundingBoxId);

            if (boundingBox == null || structureId == null || fullBoundingBoxIds.contains(boundingBoxId)) continue;
            if (!boundingBox.contains(x, y, z)) continue;

            StructureType structureType = structureTypesById.get(structureId);
            if (structureType != null && !result.contains(structureType.type())) result.add(structureType.type());
        }

        return List.copyOf(result);
    }

    /**
     * Returns dynamic structure spawn areas containing the supplied block position.
     */
    public List<DynamicSpawnAreaMatch> findDynamicSpawnAreas(BlockVector3 position) {
        Preconditions.checkNotNull(position, "position");

        List<DynamicSpawnAreaMatch> result = new ArrayList<>();

        for (DynamicSpawnArea spawnArea : dynamicSpawnAreas) {
            BoundingBox boundingBox = findBoundingBox(spawnArea.boundingBoxId());
            if (boundingBox == null || !boundingBox.contains(position.getX(), position.getY(), position.getZ())) continue;

            StructureType structureType = findStructureType(spawnArea.structureId());
            if (structureType == null) continue;
            result.add(new DynamicSpawnAreaMatch(structureType, boundingBox, StructureBoundingBoxType.fromFullBoundingBox(spawnArea.fullBoundingBox())));
        }

        return List.copyOf(result);
    }

    /**
     * Returns static structure spawn areas containing the supplied block position.
     */
    public List<StaticSpawnAreaMatch> findStaticSpawnAreas(BlockVector3 position) {
        Preconditions.checkNotNull(position, "position");

        List<StaticSpawnAreaMatch> result = new ArrayList<>();

        for (StaticSpawnArea spawnArea : staticSpawnAreas) {
            BoundingBox boundingBox = findBoundingBox(spawnArea.boundingBoxId());
            if (boundingBox == null || !boundingBox.contains(position.getX(), position.getY(), position.getZ())) continue;

            StructureType structureType = findStructureType(spawnArea.structureId());
            if (structureType == null) continue;
            result.add(new StaticSpawnAreaMatch(structureType, boundingBox, spawnArea.offset(), StructureBoundingBoxType.fromFullBoundingBox(spawnArea.fullBoundingBox())));
        }

        return List.copyOf(result);
    }

    private StructureType findStructureType(int id) {
        return structureTypesById.get(id);
    }

    private BoundingBox findBoundingBox(int id) {
        return boundingBoxesById.get(id);
    }

    /**
     * Persisted structure type keyed by its raw uint32 local ID.
     */
    public record StructureType(int id, String type) {
        public StructureType {
            Preconditions.checkNotNull(type, "type");
        }
    }

    /**
     * Persisted integer structure bounding box keyed by its raw uint32 local ID.
     */
    public record BoundingBox(int id, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        /**
         * Returns whether the block position lies inside this box using inclusive bounds.
         */
        public boolean contains(int x, int y, int z) {
            return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
        }
    }

    /**
     * Persisted dynamic spawn-area relationship.
     */
    public record DynamicSpawnArea(int boundingBoxId, int structureId, boolean fullBoundingBox) {
    }

    /**
     * Persisted static spawn-area relationship.
     */
    public record StaticSpawnArea(int boundingBoxId, int structureId, int offset, boolean fullBoundingBox) {
    }

    /**
     * Resolved dynamic spawn-area match used by structure spawning queries.
     */
    public record DynamicSpawnAreaMatch(StructureType structureType, BoundingBox boundingBox, StructureBoundingBoxType boundingBoxType) {
    }

    /**
     * Resolved static spawn-area match used by structure spawning queries.
     */
    public record StaticSpawnAreaMatch(StructureType structureType, BoundingBox boundingBox, int offset, StructureBoundingBoxType boundingBoxType) {
    }
}
