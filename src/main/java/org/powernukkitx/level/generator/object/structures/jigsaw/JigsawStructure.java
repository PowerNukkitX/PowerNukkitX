package org.powernukkitx.level.generator.object.structures.jigsaw;

import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.payload.structure.Rotation;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockJigsaw;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockStructureVoid;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.level.generator.object.structures.StructureHelper;
import org.powernukkitx.level.generator.object.structures.jigsaw.pool.StructurePool;
import org.powernukkitx.level.generator.object.structures.jigsaw.pool.StructurePoolCollection;
import org.powernukkitx.level.generator.object.structures.utils.BoundingBox;
import org.powernukkitx.level.structure.PNXStructure;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.random.RandomSourceProvider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Jigsaw Structure for PowerNukkitX
 * @author Buddelbubi
 */
@Slf4j
public abstract class JigsawStructure {

    public abstract StructurePoolCollection getStructurePoolCollection();

    public abstract String getEntryPool();

    public final void place(StructureHelper manager, RandomSourceProvider randomSourceProvider) {
        placeWithBounds(manager, randomSourceProvider);
    }

    /**
     * Places the structure and returns its ordered world-space piece bounding boxes.
     */
    public final List<BoundingBox> placeWithBounds(StructureHelper manager, RandomSourceProvider randomSourceProvider) {
        return assemble(manager, randomSourceProvider, true);
    }

    /**
     * Builds the structure layout without generating or applying world chunks and returns its ordered world-space piece bounds.
     */
    public final List<BoundingBox> collectPieceBounds(StructureHelper manager, RandomSourceProvider randomSourceProvider) {
        return assemble(manager, randomSourceProvider, false);
    }

    private List<BoundingBox> assemble(StructureHelper manager, RandomSourceProvider randomSourceProvider, boolean applyToWorld) {
        Map<String, String> poolAliases = createPoolAliases(randomSourceProvider);
        String entryPool = normalizeResourceKey(getEntryPool());
        StructurePool startPool = getStructurePoolCollection().get(entryPool);
        if (startPool == null) {
            log.debug("Jigsaw start pool not found: {}", entryPool);
            return List.of();
        }
        PlacedStructurePiece rootPiece = placeStructurePiece(
                Vector3.ZERO,
                getRandomRotation(randomSourceProvider),
                manager,
                startPool,
                randomSourceProvider,
                applyToWorld
        );
        if (rootPiece == null) {
            log.debug("Failed to place Jigsaw root piece from start pool: {}", entryPool);
            return List.of();
        }

        BlockVector3 maxDistanceCenter = getBoundingBoxCenter(rootPiece.boundingBox());
        Set<BlockVector3> connectedJigsaws = new HashSet<>();
        List<BoundingBox> occupiedBoxes = new ArrayList<>();
        occupiedBoxes.add(rootPiece.boundingBox());
        List<Beardifier.TerrainAdaptationPiece> terrainAdaptationPieces = new ArrayList<>();
        if (appliesTerrainAdaptation(rootPiece.structureName())) {
            terrainAdaptationPieces.add(
                    Beardifier.TerrainAdaptationPiece.atBoundingBoxFloor(rootPiece.boundingBox())
            );
        }
        List<PendingStructurePiece> pendingPieces = new ArrayList<>();
        pendingPieces.add(new PendingStructurePiece(rootPiece, 0, 0));

        while (!pendingPieces.isEmpty()) {
            PendingStructurePiece pending = pendingPieces.remove(0);
            if (pending.depth() >= getMaxDepth()) {
                continue;
            }

            for (JigsawReference sourceReference : getOrderedJigsaws(
                    pending.piece().sourceJigsaws(),
                    pending.piece().placedJigsaws(),
                    randomSourceProvider
            )) {
                BlockVector3 parentWorldPos = absolutePos(pending.piece().position(), sourceReference.placedJigsaw());
                if (connectedJigsaws.contains(parentWorldPos)) {
                    continue;
                }

                String sourcePoolKey = normalizeResourceKey(sourceReference.sourceJigsaw().getPool());
                String nextPoolKey = poolAliases.getOrDefault(sourcePoolKey, sourcePoolKey);
                StructurePool nextPool = getStructurePoolCollection().get(nextPoolKey);
                if (nextPool == null) {
                    log.debug(
                            "Jigsaw pool not found: {} from piece {} jigsaw name={} target={}",
                            nextPoolKey,
                            pending.piece().structureName(),
                            normalizeResourceKey(sourceReference.sourceJigsaw().getName()),
                            normalizeResourceKey(sourceReference.sourceJigsaw().getTarget())
                    );
                    continue;
                }

                Candidate candidate = findCandidate(
                        nextPool,
                        pending.piece(),
                        sourceReference,
                        manager,
                        randomSourceProvider,
                        connectedJigsaws,
                        occupiedBoxes,
                        maxDistanceCenter
                );
                if (candidate == null) {
                    continue;
                }

                PlacedStructurePiece childPiece = placeStructurePiece(
                        candidate.connection().childStructurePos().asVector3(),
                        candidate.connection().childRotation(),
                        manager,
                        candidate.structureName(),
                        candidate.childStructure(),
                        candidate.projection(),
                        occupiedBoxes,
                        pending.piece().boundingBox(),
                        maxDistanceCenter,
                        applyToWorld
                );
                if (childPiece == null) {
                    continue;
                }

                connectedJigsaws.add(parentWorldPos);
                connectedJigsaws.add(candidate.connection().childJigsawWorldPos());
                occupiedBoxes.add(childPiece.boundingBox());
                if (appliesTerrainAdaptation(childPiece.structureName())) {
                    terrainAdaptationPieces.add(new Beardifier.TerrainAdaptationPiece(
                            childPiece.boundingBox(),
                            candidate.connection().childJigsawWorldPos().getY()
                    ));
                }
                if (pending.depth() + 1 < getMaxDepth()) {
                    insertPendingPiece(
                            pendingPieces,
                            new PendingStructurePiece(childPiece, pending.depth() + 1, sourceReference.sourceJigsaw().getPlacementPriority())
                    );
                }
            }
        }
        if (applyToWorld) {
            postProcessStructure(manager, terrainAdaptationPieces);
        }

        BlockVector3 origin = manager.getOrigin();
        List<BoundingBox> worldBoxes = new ArrayList<>(occupiedBoxes.size());
        for (BoundingBox box : occupiedBoxes) {
            worldBoxes.add(box.moved(origin.getX(), origin.getY(), origin.getZ()));
        }
        return List.copyOf(worldBoxes);
    }

    private PlacedStructurePiece placeStructurePiece(
        Vector3 position,
        Rotation rotation,
        StructureHelper helper,
        StructurePool pool,
        RandomSourceProvider randomSourceProvider, boolean applyToWorld) {

        if (pool == null) {
            return null;
        }

        StructurePool.Entry selectedEntry = pool.getRandomEntry(randomSourceProvider);
        if (isEmptyPoolEntry(selectedEntry.structureName())) {
            return null;
        }
        String structureName = normalizeResourceKey(selectedEntry.structureName());
        PNXStructure structure = (PNXStructure) Registries.STRUCTURE.get(structureName);
        if (structure == null) {
            log.debug("Jigsaw structure not found: {} from pool {}", structureName, pool.getName());
            return null;
        }

        String startJigsawName = normalizeResourceKey(getStartJigsawName());
        if (!startJigsawName.isEmpty()) {
            PNXStructure.Jigsaw startJigsaw = findStartJigsaw(structure.getJigsaws(rotation), startJigsawName, randomSourceProvider);
            if (startJigsaw == null) {
                log.debug("Jigsaw start jigsaw not found: {} in {}", startJigsawName, structureName);
                return null;
            }
            position = new Vector3(
                    position.getFloorX() - startJigsaw.getX(),
                    position.getFloorY(),
                    position.getFloorZ() - startJigsaw.getZ()
            );
        }

        return placeStructurePiece(position, rotation, helper, structureName, structure, selectedEntry.projection(), null, null, null, applyToWorld);
    }

    protected abstract int getMaxDepth();

    protected String getStartJigsawName() {
        return null;
    }

    protected int getMaxDistanceFromCenter() {
        return -1;
    }

    protected Map<String, String> createPoolAliases(RandomSourceProvider randomSourceProvider) {
        return Map.of();
    }

    protected void postProcessStructurePiece(String structureName, BlockManager blockManager, PNXStructure.Jigsaw[] jigsaws) {
    }

    protected void postProcessStructure(StructureHelper helper) {
    }

    protected void postProcessStructure(StructureHelper helper, List<Beardifier.TerrainAdaptationPiece> terrainAdaptationPieces) {
        postProcessStructure(helper);
    }

    protected boolean appliesTerrainAdaptation(String structureName) {
        return true;
    }

    private PlacedStructurePiece placeStructurePiece(
        Vector3 position,
        Rotation rotation,
        StructureHelper helper,
        String structureName,
        PNXStructure structure,
        String projection,
        List<BoundingBox> occupiedBoxes,
        BoundingBox ignoredBox,
        BlockVector3 maxDistanceCenter,
        boolean applyToWorld) {

        BoundingBox currentBox = createBoundingBox(position, structure, rotation);
        if (maxDistanceCenter != null && !isWithinMaxDistance(currentBox, maxDistanceCenter)
                || occupiedBoxes != null && hasStructureCollision(helper, position, structure, rotation, occupiedBoxes, currentBox, ignoredBox)) {
            return null;
        }
        if (applyToWorld) {
            ensureChunksGenerated(helper, currentBox);
        }
        Vector3 worldPosition = toWorldPosition(helper, position);
        StructureHelper tempHelper = new StructureHelper(helper.getLevel(), new BlockVector3(0, 0, 0));
        structure.preparePlace(Position.fromObject(worldPosition), tempHelper, rotation);
        PNXStructure.Jigsaw[] placedJigsaws = structure.getJigsaws(rotation);
        PNXStructure.Jigsaw[] absoluteJigsaws = toAbsoluteJigsaws(placedJigsaws, worldPosition);
        postProcessStructurePiece(structureName, tempHelper, absoluteJigsaws);
        replaceJigsawBlocks(tempHelper, absoluteJigsaws);
        mergeAbsoluteBlocks(helper, tempHelper);
        placedJigsaws = toRelativeJigsaws(absoluteJigsaws, worldPosition);
        return new PlacedStructurePiece(structureName, structure, position, rotation, currentBox, projection, structure.getJigsaws(), placedJigsaws);
    }

    private Candidate findCandidate(StructurePool pool, PlacedStructurePiece parentPiece, JigsawReference sourceReference,
                                    StructureHelper helper, RandomSourceProvider randomSourceProvider, Set<BlockVector3> connectedJigsaws,
                                    List<BoundingBox> occupiedBoxes, BlockVector3 maxDistanceCenter) {
        CandidateSearchResult primary = findCandidateInPool(
                pool, parentPiece, sourceReference, helper, randomSourceProvider, connectedJigsaws, occupiedBoxes, maxDistanceCenter
        );
        if (primary.candidate() != null || primary.terminal()) {
            return primary.candidate();
        }

        String fallbackKey = normalizeResourceKey(pool.getFallback());
        if (fallbackKey.isEmpty()) {
            return null;
        }

        StructurePool fallbackPool = getStructurePoolCollection().get(fallbackKey);
        if (fallbackPool == null) {
            log.debug("Jigsaw fallback pool not found: {} for pool {}", fallbackKey, pool.getName());
            return null;
        }

        return findCandidateInPool(
                fallbackPool, parentPiece, sourceReference, helper, randomSourceProvider, connectedJigsaws, occupiedBoxes, maxDistanceCenter
        ).candidate();
    }

    private CandidateSearchResult findCandidateInPool(StructurePool pool, PlacedStructurePiece parentPiece,
                                                       JigsawReference sourceReference, StructureHelper helper,
                                                       RandomSourceProvider randomSourceProvider,
                                                       Set<BlockVector3> connectedJigsaws, List<BoundingBox> occupiedBoxes,
                                                       BlockVector3 maxDistanceCenter) {
        for (StructurePool.Entry candidateEntry : getCandidateEntries(pool, randomSourceProvider)) {
            if (isEmptyPoolEntry(candidateEntry.structureName())) {
                return new CandidateSearchResult(null, true);
            }

            String structureKey = normalizeResourceKey(candidateEntry.structureName());
            PNXStructure childStructure = (PNXStructure) Registries.STRUCTURE.get(structureKey);
            if (childStructure == null) {
                log.debug("Jigsaw candidate structure not found: {} from pool {}", structureKey, pool.getName());
                continue;
            }

            for (Rotation childRotation : getShuffledRotations(randomSourceProvider)) {
                Connection connection = resolveConnection(parentPiece, sourceReference, childStructure, childRotation, randomSourceProvider);
                if (connection == null || connectedJigsaws.contains(connection.childJigsawWorldPos())) {
                    continue;
                }
                if (!isWithinMaxDistance(connection.childBoundingBox(), maxDistanceCenter) || hasStructureCollision(
                        helper,
                        connection.childStructurePos().asVector3(),
                        childStructure,
                        childRotation,
                        occupiedBoxes,
                        connection.childBoundingBox(),
                        parentPiece.boundingBox()
                )) {
                    continue;
                }
                return new CandidateSearchResult(new Candidate(structureKey, childStructure, candidateEntry.projection(), connection), false);
            }
        }
        return new CandidateSearchResult(null, false);
    }

    private List<StructurePool.Entry> getCandidateEntries(StructurePool pool, RandomSourceProvider randomSourceProvider) {
        List<StructurePool.Entry> weightedEntries = new ArrayList<>();
        for (StructurePool.Entry entry : pool.entries) {
            for (int i = 0; i < entry.weight(); i++) {
                weightedEntries.add(entry);
            }
        }

        for (int i = weightedEntries.size() - 1; i > 0; i--) {
            int index = randomSourceProvider.nextExclusiveInt(i + 1);
            StructurePool.Entry value = weightedEntries.get(i);
            weightedEntries.set(i, weightedEntries.get(index));
            weightedEntries.set(index, value);
        }
        return weightedEntries;
    }

    private Connection resolveConnection(PlacedStructurePiece parentPiece, JigsawReference sourceReference, PNXStructure childStructure, Rotation childRotation, RandomSourceProvider randomSourceProvider) {
        JigsawOrientation parentOrientation = getJigsawOrientation(
                parentPiece.sourceStructure(),
                sourceReference.sourceJigsaw(),
                parentPiece.rotation()
        );
        String parentJoint = getJigsawJoint(sourceReference.sourceJigsaw(), parentOrientation.front());
        BlockVector3 parentWorldPos = absolutePos(parentPiece.position(), sourceReference.placedJigsaw());
        PNXStructure.Jigsaw[] rotatedChildJigsaws = childStructure.getJigsaws(childRotation);

        for (JigsawReference childReference : getOrderedJigsaws(
                childStructure.getJigsaws(),
                rotatedChildJigsaws,
                randomSourceProvider
        )) {
            if (!normalizeResourceKey(childReference.sourceJigsaw().getName()).equals(normalizeResourceKey(sourceReference.sourceJigsaw().getTarget()))) {
                continue;
            }

            JigsawOrientation childOrientation = getJigsawOrientation(
                    childStructure,
                    childReference.sourceJigsaw(),
                    childRotation
            );
            if (!canAttach(parentOrientation, parentJoint, sourceReference.sourceJigsaw(), childOrientation, childReference.sourceJigsaw())) {
                continue;
            }

            BlockVector3 childWorldPos = parentWorldPos.getSide(parentOrientation.front());
            BlockVector3 childStructurePos = childWorldPos.subtract(new BlockVector3(
                    childReference.placedJigsaw().getX(),
                    childReference.placedJigsaw().getY(),
                    childReference.placedJigsaw().getZ()
            ));
            return new Connection(
                    childRotation,
                    childStructurePos,
                    childWorldPos,
                    createBoundingBox(
                            childStructurePos.asVector3(),
                            childStructure,
                            childRotation
                    )
            );
        }

        return null;
    }

    private boolean canAttach(JigsawOrientation parentOrientation, String parentJoint, PNXStructure.Jigsaw parentJigsaw, JigsawOrientation childOrientation, PNXStructure.Jigsaw childJigsaw) {
        return parentOrientation.front() == childOrientation.front().getOpposite()
                && ("rollable".equals(parentJoint) || parentOrientation.top() == childOrientation.top())
                && normalizeResourceKey(parentJigsaw.getTarget()).equals(normalizeResourceKey(childJigsaw.getName()));
    }

    private JigsawOrientation getJigsawOrientation(PNXStructure structure, PNXStructure.Jigsaw jigsaw, Rotation appliedRotation) {
        int index = jigsaw.getX() + (jigsaw.getY() * structure.getSizeX()) + (jigsaw.getZ() * structure.getSizeX() * structure.getSizeY());
        int paletteIndex = (structure.getBlocks()[index] & 0xFF) - 1;
        if (paletteIndex < 0 || paletteIndex >= structure.getPalette().length) {
            throw new IllegalStateException("Invalid jigsaw palette index");
        }

        BlockState state = structure.getPalette()[paletteIndex];
        if (!(state.toBlock() instanceof BlockJigsaw blockJigsaw)) {
            throw new IllegalStateException("Jigsaw position does not point to a jigsaw block");
        }

        BlockFace front = rotateFace(blockJigsaw.getBlockFace(), appliedRotation);
        if (isHorizontal(front)) {
            return new JigsawOrientation(front, BlockFace.UP);
        }

        int blockRotation = blockJigsaw.getPropertyValue(CommonBlockProperties.ROTATION);
        BlockFace top = front == BlockFace.DOWN
                ? switch (blockRotation) {
            case 1 -> BlockFace.WEST;
            case 2 -> BlockFace.SOUTH;
            case 3 -> BlockFace.EAST;
            default -> BlockFace.NORTH;
        }
                : switch (blockRotation) {
            case 1 -> BlockFace.EAST;
            case 2 -> BlockFace.SOUTH;
            case 3 -> BlockFace.WEST;
            default -> BlockFace.NORTH;
        };
        return new JigsawOrientation(front, rotateFace(top, appliedRotation));
    }

    private String getJigsawJoint(PNXStructure.Jigsaw jigsaw, BlockFace front) {
        if (jigsaw.getJoint() != null && !jigsaw.getJoint().isBlank()) {
            return jigsaw.getJoint();
        }
        return isHorizontal(front) ? "aligned" : "rollable";
    }

    private boolean isHorizontal(BlockFace face) {
        return face == BlockFace.NORTH || face == BlockFace.SOUTH || face == BlockFace.EAST || face == BlockFace.WEST;
    }

    private BlockFace rotateFace(BlockFace face, Rotation rotation) {
        return switch (rotation) {
            case NONE -> face;
            case ROTATE_90 -> rotateCounterClockwise(face);
            case ROTATE_180 -> rotateClockwise(rotateClockwise(face));
            case ROTATE_270 -> rotateClockwise(face);
            default -> throw new IllegalStateException("invalid rotate face: " + face);
        };
    }

    private BlockFace rotateClockwise(BlockFace face) {
        return switch (face) {
            case NORTH -> BlockFace.EAST;
            case EAST -> BlockFace.SOUTH;
            case SOUTH -> BlockFace.WEST;
            case WEST -> BlockFace.NORTH;
            default -> face;
        };
    }

    private BlockFace rotateCounterClockwise(BlockFace face) {
        return switch (face) {
            case NORTH -> BlockFace.WEST;
            case WEST -> BlockFace.SOUTH;
            case SOUTH -> BlockFace.EAST;
            case EAST -> BlockFace.NORTH;
            default -> face;
        };
    }

    private BlockVector3 absolutePos(Vector3 structurePosition, PNXStructure.Jigsaw jigsaw) {
        return new BlockVector3(
                structurePosition.getFloorX() + jigsaw.getX(),
                structurePosition.getFloorY() + jigsaw.getY(),
                structurePosition.getFloorZ() + jigsaw.getZ()
        );
    }

    private String normalizeResourceKey(String key) {
        if (key == null) {
            return "";
        }

        int separator = key.indexOf(':');
        return separator >= 0 ? key.substring(separator + 1) : key;
    }

    private boolean isEmptyPoolEntry(String key) {
        String normalized = normalizeResourceKey(key);
        return normalized.isEmpty() || "empty".equals(normalized);
    }

    private PNXStructure.Jigsaw findStartJigsaw(PNXStructure.Jigsaw[] jigsaws, String startJigsawName, RandomSourceProvider randomSourceProvider) {
        List<PNXStructure.Jigsaw> shuffled = new ArrayList<>(jigsaws.length);
        for (PNXStructure.Jigsaw jigsaw : jigsaws) {
            shuffled.add(jigsaw);
        }
        for (int i = 1; i < shuffled.size(); i++) {
            int index = randomSourceProvider.nextExclusiveInt(i + 1);
            PNXStructure.Jigsaw value = shuffled.get(i);
            shuffled.set(i, shuffled.get(index));
            shuffled.set(index, value);
        }
        for (PNXStructure.Jigsaw jigsaw : shuffled) {
            if (jigsaw != null && normalizeResourceKey(jigsaw.getName()).equals(startJigsawName)) {
                return jigsaw;
            }
        }
        return null;
    }

    private List<Rotation> getShuffledRotations(RandomSourceProvider randomSourceProvider) {
        List<Rotation> rotations = new ArrayList<>(List.of(Rotation.NONE, Rotation.ROTATE_90, Rotation.ROTATE_180, Rotation.ROTATE_270));
        for (int i = rotations.size() - 1; i > 0; i--) {
            int index = randomSourceProvider.nextExclusiveInt(i + 1);
            Rotation value = rotations.get(i);
            rotations.set(i, rotations.get(index));
            rotations.set(index, value);
        }
        return rotations;
    }

    private Rotation getRandomRotation(RandomSourceProvider randomSourceProvider) {
        Rotation[] rotations = {Rotation.NONE, Rotation.ROTATE_90, Rotation.ROTATE_180, Rotation.ROTATE_270};
        return rotations[randomSourceProvider.nextExclusiveInt(rotations.length)];
    }

    private List<JigsawReference> getOrderedJigsaws(PNXStructure.Jigsaw[] sourceJigsaws, PNXStructure.Jigsaw[] placedJigsaws, RandomSourceProvider randomSourceProvider) {
        List<Integer> indices = new ArrayList<>(sourceJigsaws.length);
        for (int i = 0; i < sourceJigsaws.length; i++) {
            indices.add(i);
        }
        for (int i = indices.size() - 1; i > 0; i--) {
            int index = randomSourceProvider.nextExclusiveInt(i + 1);
            int value = indices.get(i);
            indices.set(i, indices.get(index));
            indices.set(index, value);
        }
        indices.sort(Comparator.comparingInt((Integer index) -> sourceJigsaws[index].getSelectionPriority()).reversed());

        List<JigsawReference> references = new ArrayList<>(indices.size());
        for (int index : indices) {
            references.add(new JigsawReference(sourceJigsaws[index], placedJigsaws[index]));
        }
        return references;
    }

    private void insertPendingPiece(List<PendingStructurePiece> queue, PendingStructurePiece pending) {
        int index = queue.size();
        for (int i = 0; i < queue.size(); i++) {
            if (pending.priority() > queue.get(i).priority()) {
                index = i;
                break;
            }
        }
        queue.add(index, pending);
    }

    private BlockVector3 getBoundingBoxCenter(BoundingBox boundingBox) {
        return new BlockVector3(
                boundingBox.x0 + (boundingBox.x1 - boundingBox.x0 + 1) / 2,
                boundingBox.y0 + (boundingBox.y1 - boundingBox.y0 + 1) / 2,
                boundingBox.z0 + (boundingBox.z1 - boundingBox.z0 + 1) / 2
        );
    }

    private boolean isWithinMaxDistance(BoundingBox boundingBox, BlockVector3 center) {
        int maxDistance = getMaxDistanceFromCenter();
        return maxDistance < 0
                || boundingBox.x0 >= center.getX() - maxDistance && boundingBox.x1 <= center.getX() + maxDistance
                && boundingBox.y0 >= center.getY() - maxDistance && boundingBox.y1 <= center.getY() + maxDistance
                && boundingBox.z0 >= center.getZ() - maxDistance && boundingBox.z1 <= center.getZ() + maxDistance;
    }

    private boolean hasStructureCollision(StructureHelper helper, Vector3 position, PNXStructure structure, Rotation rotation,
                                          List<BoundingBox> occupiedBoxes, BoundingBox structureBox, BoundingBox ignoredBox) {
        if (overlapsExistingBox(occupiedBoxes, structureBox, ignoredBox)) {
            return true;
        }
        return wouldReplaceExistingStructureBlock(helper, position, structure, rotation);
    }

    private boolean wouldReplaceExistingStructureBlock(StructureHelper helper, Vector3 position, PNXStructure structure, Rotation rotation) {
        byte[] blocks = structure.getBlocks();
        BlockState[] palette = structure.getPalette();

        int baseX = position.getFloorX();
        int baseY = position.getFloorY();
        int baseZ = position.getFloorZ();

        int index = 0;

        for (int z = 0; z < structure.getSizeZ(); z++) {
            for (int y = 0; y < structure.getSizeY(); y++) {
                for (int x = 0; x < structure.getSizeX(); x++) {
                    int paletteIndex = (blocks[index++] & 0xFF) - 1;

                    if (paletteIndex < 0) continue;
                    if (paletteIndex < palette.length && palette[paletteIndex].toBlock() instanceof BlockStructureVoid) continue;

                    int rx = structure.getRotatedX(x, z, rotation);
                    int rz = structure.getRotatedZ(x, z, rotation);
                    BlockVector3 worldPos = new BlockVector3(baseX + rx, baseY + y, baseZ + rz);

                    if (helper.isCached(worldPos)) return true;
                }
            }
        }

        return false;
    }

    private BoundingBox createBoundingBox(Vector3 position, PNXStructure structure, Rotation rotation) {
        return new BoundingBox(
                position.getFloorX(),
                position.getFloorY(),
                position.getFloorZ(),
                position.getFloorX() + structure.getRotatedSizeX(rotation) - 1,
                position.getFloorY() + structure.getSizeY() - 1,
                position.getFloorZ() + structure.getRotatedSizeZ(rotation) - 1
        );
    }

    private void ensureChunksGenerated(StructureHelper helper, BoundingBox relativeBox) {
        BlockVector3 origin = helper.getOrigin();
        int minChunkX = (origin.getX() + relativeBox.x0) >> 4;
        int maxChunkX = (origin.getX() + relativeBox.x1) >> 4;
        int minChunkZ = (origin.getZ() + relativeBox.z0) >> 4;
        int maxChunkZ = (origin.getZ() + relativeBox.z1) >> 4;

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                helper.getLevel().getOrGenerateChunk(chunkX, chunkZ);
            }
        }
    }

    private boolean overlapsExistingBox(List<BoundingBox> occupiedBoxes, BoundingBox box, BoundingBox ignoredBox) {
        for (BoundingBox occupiedBox : occupiedBoxes) {
            if (occupiedBox == ignoredBox) continue;

            if (strictlyIntersects(occupiedBox, box)) return true;
        }
        return false;
    }

    protected boolean strictlyIntersects(BoundingBox first, BoundingBox second) {
        return first.x1 >= second.x0 && first.x0 <= second.x1 && first.y1 >= second.y0 && first.y0 <= second.y1 && first.z1 >= second.z0 && first.z0 <= second.z1;
    }

    private void replaceJigsawBlocks(StructureHelper helper, PNXStructure.Jigsaw[] jigsaws) {
        for (PNXStructure.Jigsaw jigsaw : jigsaws) {
            if(jigsaw == null) continue;
            BlockState finalState = jigsaw.getFinalState();
            if (finalState == null || finalState.toBlock() instanceof BlockJigsaw) {
                finalState = BlockAir.STATE;
            }
            helper.setBlockStateAt(jigsaw.getX(), jigsaw.getY(), jigsaw.getZ(), finalState);
        }
    }

    private Vector3 toWorldPosition(StructureHelper helper, Vector3 relativePosition) {
        BlockVector3 origin = helper.getOrigin();
        return new Vector3(
                origin.getX() + relativePosition.getFloorX(),
                origin.getY() + relativePosition.getFloorY(),
                origin.getZ() + relativePosition.getFloorZ()
        );
    }

    private PNXStructure.Jigsaw[] toAbsoluteJigsaws(PNXStructure.Jigsaw[] jigsaws, Vector3 worldPosition) {
        PNXStructure.Jigsaw[] absoluteJigsaws = new PNXStructure.Jigsaw[jigsaws.length];
        for (int i = 0; i < jigsaws.length; i++) {
            PNXStructure.Jigsaw jigsaw = jigsaws[i];
            absoluteJigsaws[i] = jigsaw.withPosition(
                    worldPosition.getFloorX() + jigsaw.getX(),
                    worldPosition.getFloorY() + jigsaw.getY(),
                    worldPosition.getFloorZ() + jigsaw.getZ()
            );
        }
        return absoluteJigsaws;
    }

    private PNXStructure.Jigsaw[] toRelativeJigsaws(PNXStructure.Jigsaw[] jigsaws, Vector3 worldPosition) {
        ArrayList<PNXStructure.Jigsaw> relativeJigsaws = new ArrayList<>();
        for (int i = 0; i < jigsaws.length; i++) {
            PNXStructure.Jigsaw jigsaw = jigsaws[i];
            if(jigsaw == null) continue;
            relativeJigsaws.add(jigsaw.withPosition(
                    jigsaw.getX() - worldPosition.getFloorX(),
                    jigsaw.getY() - worldPosition.getFloorY(),
                    jigsaw.getZ() - worldPosition.getFloorZ()
            ));
        }
        return relativeJigsaws.toArray(PNXStructure.Jigsaw[]::new);
    }

    private void mergeAbsoluteBlocks(StructureHelper targetHelper, BlockManager absoluteBlocks) {
        BlockVector3 origin = targetHelper.getOrigin();
        absoluteBlocks.getBlocks().forEach(block -> targetHelper.setBlockStateAt(
                block.getFloorX() - origin.getX(),
                block.getFloorY() - origin.getY(),
                block.getFloorZ() - origin.getZ(),
                block.getBlockState()
        ));
        targetHelper.getHooks().addAll(absoluteBlocks.getHooks());
    }

    private record Connection(Rotation childRotation, BlockVector3 childStructurePos, BlockVector3 childJigsawWorldPos, BoundingBox childBoundingBox) {
    }

    private record Candidate(String structureName, PNXStructure childStructure, String projection, Connection connection) {
    }

    private record CandidateSearchResult(Candidate candidate, boolean terminal) {
    }

    private record JigsawOrientation(BlockFace front, BlockFace top) {
    }

    private record JigsawReference(PNXStructure.Jigsaw sourceJigsaw, PNXStructure.Jigsaw placedJigsaw) {
    }

    private record PlacedStructurePiece(String structureName, PNXStructure sourceStructure, Vector3 position, Rotation rotation, BoundingBox boundingBox, String projection, PNXStructure.Jigsaw[] sourceJigsaws, PNXStructure.Jigsaw[] placedJigsaws) {
    }

    private record PendingStructurePiece(PlacedStructurePiece piece, int depth, int priority) {
    }
}
