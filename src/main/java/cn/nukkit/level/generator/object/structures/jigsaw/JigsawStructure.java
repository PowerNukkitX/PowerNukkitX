package cn.nukkit.level.generator.object.structures.jigsaw;

import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockJigsaw;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockStructureVoid;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.object.structures.StructureHelper;
import cn.nukkit.level.generator.object.structures.jigsaw.pool.StructurePool;
import cn.nukkit.level.generator.object.structures.jigsaw.pool.StructurePoolCollection;
import cn.nukkit.level.generator.object.structures.utils.BoundingBox;
import cn.nukkit.registry.Registries;
import cn.nukkit.level.structure.PNXStructure;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.types.Rotation;
import cn.nukkit.utils.random.RandomSourceProvider;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Jigsaw Structure for PowerNukkitX
 * @author Buddelbubi
 */
public abstract class JigsawStructure {

    public abstract StructurePoolCollection getStructurePoolCollection();

    public abstract String getEntryPool();

    public final void place(StructureHelper manager, RandomSourceProvider randomSourceProvider) {
        PlacedStructurePiece rootPiece = placeStructurePiece(
                Vector3.ZERO,
                Rotation.NONE,
                manager,
                getStructurePoolCollection().get(normalizeResourceKey(getEntryPool())),
                randomSourceProvider
        );
        if (rootPiece == null) {
            return;
        }

        Set<BlockVector3> connectedJigsaws = new HashSet<>();
        List<BoundingBox> occupiedBoxes = new ArrayList<>();
        occupiedBoxes.add(rootPiece.boundingBox());
        List<PendingStructurePiece> pendingPieces = new ArrayList<>();
        pendingPieces.add(new PendingStructurePiece(rootPiece, 0, 0));

        while (!pendingPieces.isEmpty()) {
            PendingStructurePiece pending = pendingPieces.remove(0);
            if (pending.depth() >= getMaxDepth()) {
                continue;
            }

            for (JigsawReference sourceReference : getOrderedJigsaws(
                    pending.piece().sourceStructure(),
                    pending.piece().placedStructure(),
                    randomSourceProvider
            )) {
                BlockVector3 parentWorldPos = absolutePos(pending.piece().position(), sourceReference.placedJigsaw());
                if (connectedJigsaws.contains(parentWorldPos)) {
                    continue;
                }

                StructurePool nextPool = getStructurePoolCollection().get(normalizeResourceKey(sourceReference.sourceJigsaw().getPool()));
                if (nextPool == null) {
                    continue;
                }

                Candidate candidate = findCandidate(
                        nextPool,
                        pending.piece(),
                        sourceReference,
                        manager,
                        randomSourceProvider,
                        connectedJigsaws,
                        occupiedBoxes
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
                        occupiedBoxes
                );
                if (childPiece == null) {
                    continue;
                }

                connectedJigsaws.add(parentWorldPos);
                connectedJigsaws.add(candidate.connection().childJigsawWorldPos());
                occupiedBoxes.add(childPiece.boundingBox());
                if (pending.depth() + 1 < getMaxDepth()) {
                    insertPendingPiece(
                            pendingPieces,
                            new PendingStructurePiece(childPiece, pending.depth() + 1, sourceReference.sourceJigsaw().getPlacementPriority())
                    );
                }
            }
        }
        postProcessStructure(manager);
    }

    private PlacedStructurePiece placeStructurePiece(Vector3 position, Rotation rotation, StructureHelper helper, StructurePool pool,
                                                     RandomSourceProvider randomSourceProvider) {
        if (pool == null) {
            return null;
        }

        StructurePool.Entry selectedEntry = pool.getRandomEntry(randomSourceProvider);
        String structureName = normalizeResourceKey(selectedEntry.structureName());
        PNXStructure structure = (PNXStructure) Registries.STRUCTURE.get(structureName);
        if (structure == null) {
            return null;
        }

        return placeStructurePiece(position, rotation, helper, structureName, structure, selectedEntry.projection(), null);
    }

    protected abstract int getMaxDepth();

    protected void postProcessStructurePiece(String structureName, BlockManager blockManager, PNXStructure.Jigsaw[] jigsaws) {
    }

    protected void postProcessStructure(StructureHelper helper) {
    }

    private PlacedStructurePiece placeStructurePiece(Vector3 position, Rotation rotation, StructureHelper helper, String structureName,
                                                     PNXStructure structure, String projection, List<BoundingBox> occupiedBoxes) {
        PNXStructure placedStructure = rotation == Rotation.NONE ? structure : structure.rotate(rotation);
        BoundingBox currentBox = createBoundingBox(position, placedStructure);
        if (occupiedBoxes != null && hasStructureCollision(helper, position, placedStructure, occupiedBoxes, currentBox, null)) {
            return null;
        }
        Vector3 worldPosition = toWorldPosition(helper, position);
        StructureHelper tempHelper = new StructureHelper(helper.getLevel(), new BlockVector3(0, 0, 0));
        placedStructure.preparePlace(Position.fromObject(worldPosition), tempHelper);
        PNXStructure.Jigsaw[] placedJigsaws = placedStructure.getJigsaws();
        PNXStructure.Jigsaw[] localPlacedJigsaws = placedJigsaws;
        PNXStructure.Jigsaw[] absoluteJigsaws = toAbsoluteJigsaws(localPlacedJigsaws, worldPosition);
        postProcessStructurePiece(structureName, tempHelper, absoluteJigsaws);
        replaceJigsawBlocks(tempHelper, absoluteJigsaws);
        mergeAbsoluteBlocks(helper, tempHelper);
        placedJigsaws = toRelativeJigsaws(absoluteJigsaws, worldPosition);
        return new PlacedStructurePiece(structureName, structure, placedStructure, position, rotation, currentBox, projection, structure.getJigsaws(), placedJigsaws);
    }

    private Candidate findCandidate(StructurePool pool, PlacedStructurePiece parentPiece, JigsawReference sourceReference,
                                    StructureHelper helper, RandomSourceProvider randomSourceProvider, Set<BlockVector3> connectedJigsaws,
                                    List<BoundingBox> occupiedBoxes) {
        for (StructurePool.Entry candidateEntry : getCandidateEntries(pool, randomSourceProvider)) {
            String structureKey = normalizeResourceKey(candidateEntry.structureName());
            PNXStructure childStructure = (PNXStructure) Registries.STRUCTURE.get(structureKey);
            if (childStructure == null) {
                continue;
            }

            for (Rotation childRotation : getShuffledRotations(randomSourceProvider)) {
                Connection connection = resolveConnection(parentPiece, sourceReference, childStructure, candidateEntry.projection(), childRotation, helper, randomSourceProvider);
                if (connection == null || connectedJigsaws.contains(connection.childJigsawWorldPos())) {
                    continue;
                }
                if (hasStructureCollision(
                        helper,
                        connection.childStructurePos().asVector3(),
                        connection.rotatedChildStructure(),
                        occupiedBoxes,
                        connection.childBoundingBox(),
                        parentPiece.boundingBox()
                )) {
                    continue;
                }
                return new Candidate(structureKey, childStructure, candidateEntry.projection(), connection);
            }
        }
        return null;
    }

    private List<StructurePool.Entry> getCandidateEntries(StructurePool pool, RandomSourceProvider randomSourceProvider) {
        List<StructurePool.Entry> weightedEntries = new ArrayList<>();
        for (StructurePool.Entry entry : pool.entries) {
            for (int i = 0; i < entry.weight(); i++) {
                weightedEntries.add(entry);
            }
        }
        if (pool.getFallback() != null) {
            StructurePool fallbackPool = getStructurePoolCollection().get(normalizeResourceKey(pool.getFallback()));
            if (fallbackPool != null) {
                for (StructurePool.Entry entry : fallbackPool.entries) {
                    for (int i = 0; i < entry.weight(); i++) {
                        weightedEntries.add(entry);
                    }
                }
            }
        }

        for (int i = weightedEntries.size() - 1; i > 0; i--) {
            int index = randomSourceProvider.nextBoundedInt(i);
            StructurePool.Entry value = weightedEntries.get(i);
            weightedEntries.set(i, weightedEntries.get(index));
            weightedEntries.set(index, value);
        }
        return weightedEntries;
    }

    private Connection resolveConnection(PlacedStructurePiece parentPiece, JigsawReference sourceReference,
                                         PNXStructure childStructure, String childProjection, Rotation childRotation,
                                         StructureHelper helper, RandomSourceProvider randomSourceProvider) {
        JigsawOrientation parentOrientation = getJigsawOrientation(
                parentPiece.sourceStructure(),
                sourceReference.sourceJigsaw(),
                parentPiece.rotation()
        );
        String parentJoint = getJigsawJoint(sourceReference.sourceJigsaw(), parentOrientation.front());
        BlockVector3 parentWorldPos = absolutePos(parentPiece.position(), sourceReference.placedJigsaw());
        PNXStructure rotatedChild = childRotation == Rotation.NONE ? childStructure : childStructure.rotate(childRotation);

        for (JigsawReference childReference : getOrderedJigsaws(childStructure, rotatedChild, randomSourceProvider)) {
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
            childStructurePos = applyProjection(parentPiece, sourceReference, childReference, childStructurePos, childProjection, parentOrientation, helper);
            return new Connection(childRotation, childStructurePos, childWorldPos, rotatedChild, createBoundingBox(childStructurePos.asVector3(), rotatedChild));
        }

        return null;
    }

    private BlockVector3 applyProjection(PlacedStructurePiece parentPiece, JigsawReference sourceReference,
                                         JigsawReference childReference, BlockVector3 childStructurePos, String childProjection,
                                         JigsawOrientation parentOrientation, StructureHelper helper) {
        boolean sourceRigid = isRigid(parentPiece.projection());
        boolean targetRigid = isRigid(childProjection);
        int deltaY = sourceReference.placedJigsaw().getY() - childReference.placedJigsaw().getY() + parentOrientation.front().getYOffset();

        if (sourceRigid && targetRigid) {
            return new BlockVector3(
                    childStructurePos.getX(),
                    parentPiece.boundingBox().y0 + deltaY,
                    childStructurePos.getZ()
            );
        }

        BlockVector3 sourceWorldPos = toWorldPosition(helper, absolutePos(parentPiece.position(), sourceReference.placedJigsaw()).asVector3()).asBlockVector3();
        int sourceSurfaceY = helper.getLevel().getHeightMap(sourceWorldPos.getX(), sourceWorldPos.getZ());
        return new BlockVector3(
                childStructurePos.getX(),
                sourceSurfaceY - childReference.placedJigsaw().getY(),
                childStructurePos.getZ()
        );
    }

    private boolean isRigid(String projection) {
        return projection == null || "rigid".equalsIgnoreCase(normalizeResourceKey(projection));
    }

    private boolean canAttach(JigsawOrientation parentOrientation, String parentJoint, PNXStructure.Jigsaw parentJigsaw,
                              JigsawOrientation childOrientation, PNXStructure.Jigsaw childJigsaw) {
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
        BlockFace top = switch (blockRotation) {
            case 1 -> BlockFace.NORTH;
            case 2 -> BlockFace.SOUTH;
            case 3 -> BlockFace.WEST;
            default -> BlockFace.EAST;
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

    private List<Rotation> getShuffledRotations(RandomSourceProvider randomSourceProvider) {
        List<Rotation> rotations = new ArrayList<>(List.of(Rotation.NONE, Rotation.ROTATE_90, Rotation.ROTATE_180, Rotation.ROTATE_270));
        for (int i = rotations.size() - 1; i > 0; i--) {
            int index = randomSourceProvider.nextBoundedInt(i);
            Rotation value = rotations.get(i);
            rotations.set(i, rotations.get(index));
            rotations.set(index, value);
        }
        return rotations;
    }

    private List<JigsawReference> getOrderedJigsaws(PNXStructure sourceStructure, PNXStructure placedStructure,
                                                    RandomSourceProvider randomSourceProvider) {
        List<Integer> indices = new ArrayList<>(sourceStructure.getJigsaws().length);
        for (int i = 0; i < sourceStructure.getJigsaws().length; i++) {
            indices.add(i);
        }
        for (int i = indices.size() - 1; i > 0; i--) {
            int index = randomSourceProvider.nextBoundedInt(i);
            int value = indices.get(i);
            indices.set(i, indices.get(index));
            indices.set(index, value);
        }
        indices.sort(Comparator.comparingInt((Integer index) -> sourceStructure.getJigsaws()[index].getSelectionPriority()).reversed());

        List<JigsawReference> references = new ArrayList<>(indices.size());
        for (int index : indices) {
            references.add(new JigsawReference(sourceStructure.getJigsaws()[index], placedStructure.getJigsaws()[index]));
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

    private boolean hasStructureCollision(StructureHelper helper, Vector3 position, PNXStructure structure,
                                          List<BoundingBox> occupiedBoxes, BoundingBox structureBox, BoundingBox ignoredBox) {
        if (overlapsExistingBox(occupiedBoxes, structureBox, ignoredBox)) {
            return true;
        }
        return wouldReplaceExistingStructureBlock(helper, position, structure);
    }

    private boolean wouldReplaceExistingStructureBlock(StructureHelper helper, Vector3 position, PNXStructure structure) {
        for (PNXStructure.StructureBlockInstance block : structure.getBlockInstances()) {
            if (block.state.toBlock() instanceof BlockStructureVoid) {
                continue;
            }
            BlockVector3 worldPos = new BlockVector3(
                    position.getFloorX() + block.x,
                    position.getFloorY() + block.y,
                    position.getFloorZ() + block.z
            );
            if (helper.isCached(worldPos)) {
                return true;
            }
        }
        return false;
    }

    private BoundingBox createBoundingBox(Vector3 position, PNXStructure structure) {
        return new BoundingBox(
                position.getFloorX(),
                position.getFloorY(),
                position.getFloorZ(),
                position.getFloorX() + structure.getSizeX() - 1,
                position.getFloorY() + structure.getSizeY() - 1,
                position.getFloorZ() + structure.getSizeZ() - 1
        );
    }

    private boolean overlapsExistingBox(List<BoundingBox> occupiedBoxes, BoundingBox box, BoundingBox ignoredBox) {
        for (BoundingBox occupiedBox : occupiedBoxes) {
            if (occupiedBox == ignoredBox) {
                continue;
            }
            if (strictlyIntersects(occupiedBox, box)) {
                return true;
            }
        }
        return false;
    }

    protected boolean strictlyIntersects(BoundingBox first, BoundingBox second) {
        return first.x1 > second.x0 && first.x0 < second.x1
                && first.y1 > second.y0 && first.y0 < second.y1
                && first.z1 > second.z0 && first.z0 < second.z1;
    }

    private void replaceJigsawBlocks(StructureHelper helper, PNXStructure.Jigsaw[] jigsaws) {
        for (PNXStructure.Jigsaw jigsaw : jigsaws) {
            BlockState finalState = jigsaw.getFinalState();
            if (finalState == null || finalState.toBlock() instanceof BlockJigsaw) {
                finalState = BlockAir.STATE;
            }
            helper.setBlockStateAt(
                    jigsaw.getX(),
                    jigsaw.getY(),
                    jigsaw.getZ(),
                    finalState
            );
        }
    }

    private BlockState getJigsawBlockState(PNXStructure structure, PNXStructure.Jigsaw jigsaw) {
        int index = jigsaw.getX() + (jigsaw.getY() * structure.getSizeX()) + (jigsaw.getZ() * structure.getSizeX() * structure.getSizeY());
        int paletteIndex = (structure.getBlocks()[index] & 0xFF) - 1;
        if (paletteIndex < 0 || paletteIndex >= structure.getPalette().length) {
            throw new IllegalStateException("Invalid jigsaw palette index");
        }

        BlockState state = structure.getPalette()[paletteIndex];
        if (!(state.toBlock() instanceof BlockJigsaw)) {
            throw new IllegalStateException("Jigsaw position does not point to a jigsaw block");
        }
        return state;
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
        PNXStructure.Jigsaw[] relativeJigsaws = new PNXStructure.Jigsaw[jigsaws.length];
        for (int i = 0; i < jigsaws.length; i++) {
            PNXStructure.Jigsaw jigsaw = jigsaws[i];
            relativeJigsaws[i] = jigsaw.withPosition(
                    jigsaw.getX() - worldPosition.getFloorX(),
                    jigsaw.getY() - worldPosition.getFloorY(),
                    jigsaw.getZ() - worldPosition.getFloorZ()
            );
        }
        return relativeJigsaws;
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

    private record Connection(Rotation childRotation, BlockVector3 childStructurePos, BlockVector3 childJigsawWorldPos,
                              PNXStructure rotatedChildStructure, BoundingBox childBoundingBox) {
    }

    private record Candidate(String structureName, PNXStructure childStructure, String projection, Connection connection) {
    }

    private record JigsawOrientation(BlockFace front, BlockFace top) {
    }

    private record JigsawReference(PNXStructure.Jigsaw sourceJigsaw, PNXStructure.Jigsaw placedJigsaw) {
    }

    private record PlacedStructurePiece(String structureName, PNXStructure sourceStructure, PNXStructure placedStructure,
                                        Vector3 position, Rotation rotation, BoundingBox boundingBox, String projection,
                                        PNXStructure.Jigsaw[] sourceJigsaws, PNXStructure.Jigsaw[] placedJigsaws) {
    }

    private record PendingStructurePiece(PlacedStructurePiece piece, int depth, int priority) {
    }
}
