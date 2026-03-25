package cn.nukkit.level.generator.object.structures.jigsaw;

import cn.nukkit.block.BlockJigsaw;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockStructureVoid;
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
import java.util.List;
import java.util.Set;

public abstract class JigsawStructure {

    public abstract StructurePoolCollection getStructurePoolCollection();

    public abstract String getEntryPool();

    public final void place(StructureHelper manager, RandomSourceProvider randomSourceProvider) {
        placeStructure(
                Vector3.ZERO,
                Rotation.NONE,
                manager,
                getStructurePoolCollection().get(normalizeResourceKey(getEntryPool())),
                randomSourceProvider,
                new HashSet<>(),
                new ArrayList<>(),
                0
        );
        postProcessStructure(manager);
    }

    protected void placeStructure(Vector3 position, Rotation rotation, StructureHelper helper, StructurePool pool,
                                  RandomSourceProvider randomSourceProvider, Set<BlockVector3> connectedJigsaws,
                                  List<BoundingBox> occupiedBoxes, int depth) {
        if (pool == null || depth > getMaxDepth()) {
            return;
        }

        String structureName = normalizeResourceKey(pool.getStructureKey(randomSourceProvider));
        PNXStructure structure = (PNXStructure) Registries.STRUCTURE.get(structureName);
        if (structure == null) {
            return;
        }

        placeStructure(position, rotation, helper, structureName, structure, randomSourceProvider, connectedJigsaws, occupiedBoxes, depth);
    }

    protected abstract int getMaxDepth();

    protected void postProcessStructurePiece(String structureName, BlockManager blockManager, PNXStructure.Jigsaw[] jigsaws) {
    }

    protected void postProcessStructure(StructureHelper helper) {
    }

    private void placeStructure(Vector3 position, Rotation rotation, StructureHelper helper, String structureName, PNXStructure structure,
                                RandomSourceProvider randomSourceProvider, Set<BlockVector3> connectedJigsaws,
                                List<BoundingBox> occupiedBoxes, int depth) {
        PNXStructure placedStructure = rotation == Rotation.NONE ? structure : structure.rotate(rotation);
        BoundingBox currentBox = createBoundingBox(position, placedStructure);
        if (hasStructureCollision(helper, position, placedStructure, occupiedBoxes, currentBox, null)) {
            return;
        }
        occupiedBoxes.add(currentBox);
        Vector3 worldPosition = toWorldPosition(helper, position);
        StructureHelper tempHelper = new StructureHelper(helper.getLevel(), new BlockVector3(0, 0, 0));
        placedStructure.preparePlace(Position.fromObject(worldPosition), tempHelper);
        PNXStructure.Jigsaw[] placedJigsaws = placedStructure.getJigsaws();
        PNXStructure.Jigsaw[] absoluteJigsaws = toAbsoluteJigsaws(placedJigsaws, worldPosition);
        postProcessStructurePiece(structureName, tempHelper, absoluteJigsaws);
        replaceJigsawBlocks(tempHelper, absoluteJigsaws);
        mergeAbsoluteBlocks(helper, tempHelper);
        placedJigsaws = toRelativeJigsaws(absoluteJigsaws, worldPosition);

        PNXStructure.Jigsaw[] sourceJigsaws = structure.getJigsaws();

        for (int i = 0; i < sourceJigsaws.length; i++) {
            PNXStructure.Jigsaw sourceJigsaw = sourceJigsaws[i];
            PNXStructure.Jigsaw placedJigsaw = placedJigsaws[i];
            BlockVector3 parentWorldPos = absolutePos(position, placedJigsaw);
            if (connectedJigsaws.contains(parentWorldPos)) {
                continue;
            }

            StructurePool nextPool = getStructurePoolCollection().get(normalizeResourceKey(sourceJigsaw.getPool()));
            if (nextPool == null) {
                continue;
            }

            Candidate candidate = findCandidate(
                    nextPool,
                    position,
                    structure,
                    rotation,
                    sourceJigsaw,
                    placedJigsaw,
                    helper,
                    randomSourceProvider,
                    connectedJigsaws,
                    occupiedBoxes,
                    currentBox
            );
            if (candidate == null) {
                continue;
            }

            connectedJigsaws.add(parentWorldPos);
            connectedJigsaws.add(candidate.connection().childJigsawWorldPos());
            if (depth < getMaxDepth()) {
                placeStructure(
                        candidate.connection().childStructurePos().asVector3(),
                        candidate.connection().childRotation(),
                        helper,
                        candidate.structureName(),
                        candidate.childStructure(),
                        randomSourceProvider,
                        connectedJigsaws,
                        occupiedBoxes,
                        depth + 1
                );
            }
        }

    }

    private Candidate findCandidate(StructurePool pool, Vector3 parentPosition, PNXStructure parentStructure, Rotation parentRotation,
                                    PNXStructure.Jigsaw parentSourceJigsaw, PNXStructure.Jigsaw parentPlacedJigsaw,
                                    StructureHelper helper, RandomSourceProvider randomSourceProvider, Set<BlockVector3> connectedJigsaws,
                                    List<BoundingBox> occupiedBoxes, BoundingBox currentBox) {
        for (String structureKey : getCandidateStructureKeys(pool, randomSourceProvider)) {
            PNXStructure childStructure = (PNXStructure) Registries.STRUCTURE.get(structureKey);
            if (childStructure == null) {
                continue;
            }

            Connection connection = resolveConnection(parentPosition, parentStructure, parentRotation, parentSourceJigsaw, parentPlacedJigsaw, childStructure);
            if (connection == null || connectedJigsaws.contains(connection.childJigsawWorldPos())) {
                continue;
            }
            if (hasStructureCollision(
                    helper,
                    connection.childStructurePos().asVector3(),
                    connection.rotatedChildStructure(),
                    occupiedBoxes,
                    connection.childBoundingBox(),
                    currentBox
            )) {
                continue;
            }
            return new Candidate(structureKey, childStructure, connection);
        }
        return null;
    }

    private List<String> getCandidateStructureKeys(StructurePool pool, RandomSourceProvider randomSourceProvider) {
        List<String> weightedKeys = new ArrayList<>();
        for (StructurePool.Entry entry : pool.entries) {
            for (int i = 0; i < entry.weight(); i++) {
                weightedKeys.add(entry.structureName());
            }
        }

        List<String> result = new ArrayList<>();
        while (!weightedKeys.isEmpty()) {
            int index = randomSourceProvider.nextBoundedInt(weightedKeys.size() - 1);
            String selected = weightedKeys.get(index);
            result.add(selected);
            weightedKeys.removeIf(selected::equals);
        }
        return result;
    }

    private Connection resolveConnection(Vector3 parentPosition, PNXStructure parentStructure, Rotation parentRotation,
                                         PNXStructure.Jigsaw parentSourceJigsaw, PNXStructure.Jigsaw parentPlacedJigsaw,
                                         PNXStructure childStructure) {
        BlockFace parentFace = getJigsawFace(parentStructure, parentSourceJigsaw, parentRotation);
        BlockVector3 parentWorldPos = absolutePos(parentPosition, parentPlacedJigsaw);
        PNXStructure.Jigsaw[] childSourceJigsaws = childStructure.getJigsaws();

        for (int childIndex = 0; childIndex < childSourceJigsaws.length; childIndex++) {
            PNXStructure.Jigsaw childSourceJigsaw = childSourceJigsaws[childIndex];
            if (!normalizeResourceKey(childSourceJigsaw.getName()).equals(normalizeResourceKey(parentSourceJigsaw.getTarget()))) {
                continue;
            }

            for (Rotation childRotation : Rotation.values()) {
                BlockFace childFace = getJigsawFace(childStructure, childSourceJigsaw, childRotation);
                if (childFace != parentFace.getOpposite()) {
                    continue;
                }

                PNXStructure rotatedChild = childRotation == Rotation.NONE ? childStructure : childStructure.rotate(childRotation);
                PNXStructure.Jigsaw childPlacedJigsaw = rotatedChild.getJigsaws()[childIndex];
                BlockVector3 childWorldPos = parentWorldPos.getSide(parentFace);
                BlockVector3 childStructurePos = childWorldPos.subtract(new BlockVector3(
                        childPlacedJigsaw.getX(),
                        childPlacedJigsaw.getY(),
                        childPlacedJigsaw.getZ()
                ));
                return new Connection(childRotation, childStructurePos, childWorldPos, rotatedChild, createBoundingBox(childStructurePos.asVector3(), rotatedChild));
            }
        }

        return null;
    }

    private BlockFace getJigsawFace(PNXStructure structure, PNXStructure.Jigsaw jigsaw, Rotation rotation) {
        int index = jigsaw.getX() + (jigsaw.getY() * structure.getSizeX()) + (jigsaw.getZ() * structure.getSizeX() * structure.getSizeY());
        int paletteIndex = (structure.getBlocks()[index] & 0xFF) - 1;
        if (paletteIndex < 0 || paletteIndex >= structure.getPalette().length) {
            throw new IllegalStateException("Invalid jigsaw palette index");
        }

        BlockState state = structure.getPalette()[paletteIndex];
        if (!(state.toBlock() instanceof BlockJigsaw blockJigsaw)) {
            throw new IllegalStateException("Jigsaw position does not point to a jigsaw block");
        }

        return rotateFace(blockJigsaw.getBlockFace(), rotation);
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

    private boolean strictlyIntersects(BoundingBox first, BoundingBox second) {
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
    }

    private record Connection(Rotation childRotation, BlockVector3 childStructurePos, BlockVector3 childJigsawWorldPos,
                              PNXStructure rotatedChildStructure, BoundingBox childBoundingBox) {
    }

    private record Candidate(String structureName, PNXStructure childStructure, Connection connection) {
    }
}
