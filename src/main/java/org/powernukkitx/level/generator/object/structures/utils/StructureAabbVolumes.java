package org.powernukkitx.level.generator.object.structures.utils;

import com.google.common.base.Preconditions;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.structure.AabbVolumes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Adds structure AABB volume records to generated chunks.
 *
 * @author Curse
 */
public final class StructureAabbVolumes {

    private StructureAabbVolumes() {
    }

    /**
     * Adds dynamic structure AABB records to every chunk intersecting the ordered world-space piece bounds.
     */
    public static void addDynamic(Level level, String structureType, List<BoundingBox> pieces) {
        if (pieces.size() == 0) return;

        BoundingBox structureBox = BoundingBox.getUnknownBox();
        for (BoundingBox piece : pieces) {
            structureBox.expand(piece);
        }

        for (int chunkX = structureBox.x0 >> 4; chunkX <= structureBox.x1 >> 4; chunkX++) {
            for (int chunkZ = structureBox.z0 >> 4; chunkZ <= structureBox.z1 >> 4; chunkZ++) {
                IChunk chunk = level.getChunk(chunkX, chunkZ, true);
                if (chunk != null) {
                    addDynamicBounds(chunk, structureType, structureBox, pieces);
                }
            }
        }
    }

    /**
     * Adds dynamic structure AABB records to the supplied chunk from ordered world-space piece bounds.
     */
    public static void addDynamic(IChunk chunk, String structureType, List<BoundingBox> pieces) {
        if (pieces.size() == 0) return;

        BoundingBox structureBox = BoundingBox.getUnknownBox();
        for (BoundingBox piece : pieces) {
            structureBox.expand(piece);
        }
        addDynamicBounds(chunk, structureType, structureBox, pieces);
    }

    /**
     * Replaces all dynamic AABB records for one structure type in the supplied chunk.
     */
    public static void replaceDynamic(IChunk chunk, String structureType, List<DynamicStructure> structures) {
        Preconditions.checkNotNull(chunk, "chunk");
        Preconditions.checkNotNull(structureType, "structureType");
        Preconditions.checkNotNull(structures, "structures");

        List<DynamicArea> expected = new ArrayList<>();
        for (DynamicStructure structure : structures) {
            ClippedBounds full = clip(chunk, structure.fullBoundingBox());
            if (full == null) continue;

            expected.add(new DynamicArea(full, true));
            for (BoundingBox piece : structure.pieces()) {
                ClippedBounds clipped = clip(chunk, piece);
                if (clipped != null) {
                    expected.add(new DynamicArea(clipped, false));
                }
            }
        }

        synchronized (chunk) {
            AabbVolumes current = chunk.getAabbVolumes();
            Set<Integer> structureIds = new HashSet<>();
            Integer firstStructureId = null;
            for (AabbVolumes.StructureType existing : current.getStructureTypes()) {
                if (!existing.type().equals(structureType)) continue;
                structureIds.add(existing.id());
                if (firstStructureId == null) {
                    firstStructureId = existing.id();
                }
            }

            if (matchesDynamic(current, structureIds, expected)) {
                return;
            }

            List<AabbVolumes.StructureType> structureTypes = new ArrayList<>(current.getStructureTypes());
            List<AabbVolumes.BoundingBox> boundingBoxes = new ArrayList<>(current.getBoundingBoxes());
            List<AabbVolumes.DynamicSpawnArea> dynamicSpawnAreas = new ArrayList<>(current.getDynamicSpawnAreas());
            List<AabbVolumes.StaticSpawnArea> staticSpawnAreas = new ArrayList<>(current.getStaticSpawnAreas());

            Set<Integer> removedBoundingBoxIds = new HashSet<>();
            dynamicSpawnAreas.removeIf(spawnArea -> {
                if (!structureIds.contains(spawnArea.structureId())) return false;
                removedBoundingBoxIds.add(spawnArea.boundingBoxId());
                return true;
            });

            Set<Integer> referencedBoundingBoxIds = new HashSet<>();
            for (AabbVolumes.DynamicSpawnArea spawnArea : dynamicSpawnAreas) {
                referencedBoundingBoxIds.add(spawnArea.boundingBoxId());
            }
            for (AabbVolumes.StaticSpawnArea spawnArea : staticSpawnAreas) {
                referencedBoundingBoxIds.add(spawnArea.boundingBoxId());
            }
            boundingBoxes.removeIf(boundingBox ->
                    removedBoundingBoxIds.contains(boundingBox.id()) && !referencedBoundingBoxIds.contains(boundingBox.id()));

            int nextId = nextEntityId(current);
            Integer retainedStructureId = expected.size() == 0 ? null : firstStructureId;
            if (expected.size() > 0 && retainedStructureId == null) {
                retainedStructureId = nextId++;
                structureTypes.add(new AabbVolumes.StructureType(retainedStructureId, structureType));
            }

            Set<Integer> referencedStructureIds = new HashSet<>();
            for (AabbVolumes.DynamicSpawnArea spawnArea : dynamicSpawnAreas) {
                referencedStructureIds.add(spawnArea.structureId());
            }
            for (AabbVolumes.StaticSpawnArea spawnArea : staticSpawnAreas) {
                referencedStructureIds.add(spawnArea.structureId());
            }
            if (retainedStructureId != null) {
                referencedStructureIds.add(retainedStructureId);
            }

            Integer finalRetainedStructureId = retainedStructureId;
            structureTypes.removeIf(existing -> existing.type().equals(structureType)
                    && !referencedStructureIds.contains(existing.id())
                    && (finalRetainedStructureId == null || existing.id() != finalRetainedStructureId));

            if (retainedStructureId != null) {
                int targetStructureId = retainedStructureId;
                for (DynamicArea area : expected) {
                    int boundingBoxId = nextId++;
                    boundingBoxes.add(area.bounds().toBoundingBox(boundingBoxId));
                    dynamicSpawnAreas.add(new AabbVolumes.DynamicSpawnArea(boundingBoxId, targetStructureId, area.fullBoundingBox()));
                }
            }

            chunk.setAabbVolumes(new AabbVolumes(structureTypes, boundingBoxes, dynamicSpawnAreas, staticSpawnAreas));
        }
    }

    static void addDynamic(IChunk chunk, String structureType, BoundingBox structureBox, List<StructurePiece> pieces) {
        List<BoundingBox> pieceBounds = new ArrayList<>(pieces.size());
        for (StructurePiece piece : pieces) {
            pieceBounds.add(piece.getBoundingBox());
        }
        addDynamicBounds(chunk, structureType, structureBox, pieceBounds);
    }

    private static void addDynamicBounds(IChunk chunk, String structureType, BoundingBox structureBox, List<BoundingBox> pieces) {
        ClippedBounds full = clip(chunk, structureBox);
        if (full == null) return;

        synchronized (chunk) {
            AabbVolumes current = chunk.getAabbVolumes();
            List<AabbVolumes.StructureType> structureTypes = new ArrayList<>(current.getStructureTypes());
            List<AabbVolumes.BoundingBox> boundingBoxes = new ArrayList<>(current.getBoundingBoxes());
            List<AabbVolumes.DynamicSpawnArea> dynamicSpawnAreas = new ArrayList<>(current.getDynamicSpawnAreas());
            List<AabbVolumes.StaticSpawnArea> staticSpawnAreas = new ArrayList<>(current.getStaticSpawnAreas());

            int nextId = nextEntityId(current);
            Integer structureId = null;
            for (AabbVolumes.StructureType existing : structureTypes) {
                if (existing.type().equals(structureType)) {
                    structureId = existing.id();
                    break;
                }
            }

            if (structureId == null) {
                structureId = nextId++;
                structureTypes.add(new AabbVolumes.StructureType(structureId, structureType));
            }

            int fullId = nextId++;
            boundingBoxes.add(full.toBoundingBox(fullId));
            dynamicSpawnAreas.add(new AabbVolumes.DynamicSpawnArea(fullId, structureId, true));

            for (BoundingBox piece : pieces) {
                ClippedBounds clipped = clip(chunk, piece);
                if (clipped == null) continue;

                int boundingBoxId = nextId++;
                boundingBoxes.add(clipped.toBoundingBox(boundingBoxId));
                dynamicSpawnAreas.add(new AabbVolumes.DynamicSpawnArea(boundingBoxId, structureId, false));
            }

            chunk.setAabbVolumes(new AabbVolumes(structureTypes, boundingBoxes, dynamicSpawnAreas, staticSpawnAreas));
        }
    }

    private static boolean matchesDynamic(AabbVolumes current, Set<Integer> structureIds, List<DynamicArea> expected) {
        if (expected.size() == 0 && structureIds.size() > 0 && !hasStructureReference(current, structureIds)) {
            return false;
        }

        Map<Integer, AabbVolumes.BoundingBox> boundingBoxes = new HashMap<>();
        for (AabbVolumes.BoundingBox boundingBox : current.getBoundingBoxes()) {
            boundingBoxes.put(boundingBox.id(), boundingBox);
        }

        Map<AreaKey, Integer> actual = new HashMap<>();
        int actualCount = 0;
        for (AabbVolumes.DynamicSpawnArea spawnArea : current.getDynamicSpawnAreas()) {
            if (!structureIds.contains(spawnArea.structureId())) continue;

            AabbVolumes.BoundingBox boundingBox = boundingBoxes.get(spawnArea.boundingBoxId());
            if (boundingBox == null) return false;

            actual.merge(toAreaKey(boundingBox, spawnArea.fullBoundingBox()), 1, Integer::sum);
            actualCount++;
        }

        if (actualCount != expected.size()) return false;

        for (DynamicArea area : expected) {
            AreaKey key = toAreaKey(area.bounds(), area.fullBoundingBox());
            Integer count = actual.get(key);
            if (count == null) return false;
            if (count == 1) {
                actual.remove(key);
            } else {
                actual.put(key, count - 1);
            }
        }

        return actual.size() == 0;
    }

    private static boolean hasStructureReference(AabbVolumes current, Set<Integer> structureIds) {
        for (AabbVolumes.DynamicSpawnArea spawnArea : current.getDynamicSpawnAreas()) {
            if (structureIds.contains(spawnArea.structureId())) return true;
        }
        for (AabbVolumes.StaticSpawnArea spawnArea : current.getStaticSpawnAreas()) {
            if (structureIds.contains(spawnArea.structureId())) return true;
        }
        return false;
    }

    private static AreaKey toAreaKey(AabbVolumes.BoundingBox boundingBox, boolean fullBoundingBox) {
        return new AreaKey(
                boundingBox.minX(),
                boundingBox.minY(),
                boundingBox.minZ(),
                boundingBox.maxX(),
                boundingBox.maxY(),
                boundingBox.maxZ(),
                fullBoundingBox
        );
    }

    private static AreaKey toAreaKey(ClippedBounds boundingBox, boolean fullBoundingBox) {
        return new AreaKey(
                boundingBox.minX(),
                boundingBox.minY(),
                boundingBox.minZ(),
                boundingBox.maxX(),
                boundingBox.maxY(),
                boundingBox.maxZ(),
                fullBoundingBox
        );
    }

    private static int nextEntityId(AabbVolumes volumes) {
        int nextId = 0;

        for (AabbVolumes.StructureType structureType : volumes.getStructureTypes()) {
            nextId = Math.max(nextId, structureType.id() + 1);
        }

        for (AabbVolumes.BoundingBox boundingBox : volumes.getBoundingBoxes()) {
            nextId = Math.max(nextId, boundingBox.id() + 1);
        }

        return nextId;
    }

    private static ClippedBounds clip(IChunk chunk, BoundingBox boundingBox) {
        int chunkMinX = chunk.getX() << 4;
        int chunkMinZ = chunk.getZ() << 4;
        int minX = Math.max(boundingBox.x0, chunkMinX);
        int minY = Math.max(boundingBox.y0, chunk.getDimensionData().getMinHeight());
        int minZ = Math.max(boundingBox.z0, chunkMinZ);
        int maxX = Math.min(boundingBox.x1, chunkMinX + 15);
        int maxY = Math.min(boundingBox.y1, chunk.getDimensionData().getMaxHeight());
        int maxZ = Math.min(boundingBox.z1, chunkMinZ + 15);

        if (minX > maxX || minY > maxY || minZ > maxZ) return null;
        return new ClippedBounds(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * Canonical bounds for one dynamic structure instance.
     */
    public record DynamicStructure(BoundingBox fullBoundingBox, List<BoundingBox> pieces) {

        public DynamicStructure {
            Preconditions.checkNotNull(fullBoundingBox, "fullBoundingBox");
            pieces = List.copyOf(Preconditions.checkNotNull(pieces, "pieces"));
        }

        /**
         * Creates canonical bounds from a structure start.
         */
        public static DynamicStructure fromStart(StructureStart start) {
            Preconditions.checkNotNull(start, "start");

            synchronized (start.getPieces()) {
                List<BoundingBox> pieceBounds = new ArrayList<>(start.getPieces().size());
                for (StructurePiece piece : start.getPieces()) {
                    pieceBounds.add(piece.getBoundingBox());
                }
                return new DynamicStructure(start.getBoundingBox(), pieceBounds);
            }
        }

        /**
         * Creates canonical bounds from ordered piece bounds.
         */
        public static DynamicStructure fromPieces(List<BoundingBox> pieces) {
            Preconditions.checkNotNull(pieces, "pieces");
            Preconditions.checkArgument(pieces.size() > 0, "pieces must not be empty");

            BoundingBox fullBoundingBox = BoundingBox.getUnknownBox();
            for (BoundingBox piece : pieces) {
                fullBoundingBox.expand(piece);
            }
            return new DynamicStructure(fullBoundingBox, pieces);
        }
    }

    private record DynamicArea(ClippedBounds bounds, boolean fullBoundingBox) {
    }

    private record AreaKey(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean fullBoundingBox) {
    }

    private record ClippedBounds(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {

        private AabbVolumes.BoundingBox toBoundingBox(int id) {
            return new AabbVolumes.BoundingBox(id, minX, minY, minZ, maxX, maxY, maxZ);
        }
    }
}
