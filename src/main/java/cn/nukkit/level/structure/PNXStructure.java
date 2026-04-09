package cn.nukkit.level.structure;

import cn.nukkit.block.BlockState;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.StructureRotationUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.structure.Mirror;
import org.cloudburstmc.protocol.bedrock.data.structure.Rotation;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Lightweight structure representation for Bedrock-style palettes.
 * Stores only palette + blocks array, no huge object list.
 */
@Getter
@Slf4j
public class PNXStructure extends AbstractStructure {

    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;
    private BlockState[] palette;
    private final byte[] blocks; // indices into palette (+1), 0 = structure void
    private final Jigsaw[] jigsaws;

    private PNXStructure(int sizeX, int sizeY, int sizeZ, BlockState[] palette, byte[] blocks, Jigsaw[] jigsaws) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.palette = palette;
        this.blocks = blocks;
        this.jigsaws = jigsaws;
    }

    public BlockVector3 getBounds() {
        return new BlockVector3(sizeX, sizeY, sizeZ);
    }

    public static PNXStructure fromNbt(NbtMap nbt) {
        int[] sizeNbt = nbt.getIntArray("size");
        int sizeX = sizeNbt.length > 0 ? sizeNbt[0] : 0;
        int sizeY = sizeNbt.length > 1 ? sizeNbt[1] : 0;
        int sizeZ = sizeNbt.length > 2 ? sizeNbt[2] : 0;

        // --- palette (direct BlockState array) ---
        List<Integer> paletteNbt = nbt.getList("palette", NbtType.INT);
        BlockState[] palette = new BlockState[paletteNbt.size()];
        for (int i = 0; i < paletteNbt.size(); i++) {
            int hash = paletteNbt.get(i);
            BlockState state = Registries.BLOCKSTATE.get(hash);
            if (state == null) {
                log.warn("Unknown block state hash in structure palette: {}", hash);
                state = STATE_UNKNOWN;
            }
            palette[i] = state;
        }

        // --- blocks (raw indices) ---
        byte[] blocks = nbt.getByteArray("blocks");
        Jigsaw[] jigsaws = nbt.getList("jigsaw", NbtType.COMPOUND).stream().map(Jigsaw::new).toArray(Jigsaw[]::new);
        return new PNXStructure(sizeX, sizeY, sizeZ, palette, blocks, jigsaws);
    }

    public static CompletableFuture<PNXStructure> fromNbtAsync(NbtMap nbt) {
        return CompletableFuture.supplyAsync(() -> fromNbt(nbt));
    }

    public void setBlock(int x, int y, int z, BlockState state) {
        checkBounds(x, y, z);

        if (state == null || state == STATE_STRUCTURE_VOID) {
            blocks[flattenIndex(x, y, z)] = 0;
            return;
        }

        int paletteIndex = findPaletteIndex(state);
        if (paletteIndex < 0) {
            if (palette.length >= 255) {
                throw new IllegalStateException("PNXStructure palette limit exceeded at 255 entries");
            }
            paletteIndex = palette.length;
            palette = Arrays.copyOf(palette, palette.length + 1);
            palette[paletteIndex] = state;
        }

        blocks[flattenIndex(x, y, z)] = (byte) (paletteIndex + 1);
    }

    /**
     * Lazily iterate block instances instead of storing millions in memory.
     */
    public Iterable<StructureBlockInstance> getBlockInstances() {
        return () -> new Iterator<>() {
            private int idx = 0;

            @Override
            public boolean hasNext() {
                return idx < blocks.length;
            }

            @Override
            public StructureBlockInstance next() {
                int index = idx++;
                int x = index % sizeX;
                int y = (index / sizeX) % sizeY;
                int z = index / (sizeX * sizeY);

                int paletteIndex = (blocks[index] & 0xFF) - 1;
                BlockState state;
                if (paletteIndex == -1) {
                    state = STATE_STRUCTURE_VOID;
                } else if (paletteIndex < 0 || paletteIndex >= palette.length) {
                    state = STATE_UNKNOWN;
                } else {
                    state = palette[paletteIndex];
                }

                return new StructureBlockInstance(x, y, z, state);
            }
        };
    }

    @Override
    public void preparePlace(Position position, BlockManager blockManager) {
        placeBlocks(position, blockManager, getBlockInstances(), new BlockAccessor<StructureBlockInstance>() {
            @Override
            public int x(StructureBlockInstance block) {
                return block.x;
            }

            @Override
            public int y(StructureBlockInstance block) {
                return block.y;
            }

            @Override
            public int z(StructureBlockInstance block) {
                return block.z;
            }

            @Override
            public BlockState state(StructureBlockInstance block) {
                return block.state;
            }
        });
    }

    @Override
    public void place(Position position, boolean includeEntities, BlockManager blockManager) {
        preparePlace(position, blockManager);
        blockManager.applySubChunkUpdate();
    }

    @Override
    public PNXStructure rotate(Rotation rotation) {
        return rotate(rotation, inverseRotation(rotation));
    }

    public PNXStructure rotate(Rotation geometryRotation, Rotation stateRotation) {
        if (geometryRotation == Rotation.NONE && stateRotation == Rotation.NONE) return this;

        int newSizeX = rotatedSizeX(sizeX, sizeZ, geometryRotation);
        int newSizeZ = rotatedSizeZ(sizeX, sizeZ, geometryRotation);
        BlockState[] rotatedPalette = new BlockState[palette.length];
        for (int i = 0; i < palette.length; i++) {
            BlockState state = palette[i];
            if (state == STATE_STRUCTURE_VOID || state == STATE_UNKNOWN) {
                rotatedPalette[i] = state;
                continue;
            }
            rotatedPalette[i] = switch (stateRotation) {
                case ROTATE_90 -> StructureRotationUtil.clockwise90(state);
                case ROTATE_180 -> StructureRotationUtil.clockwise180(state);
                case ROTATE_270 -> StructureRotationUtil.counterclockwise90(state);
                default -> state;
            };
        }

        byte[] rotatedBlocks = new byte[blocks.length];

        for (int idx = 0; idx < blocks.length; idx++) {
            int x = idx % sizeX;
            int y = (idx / sizeX) % sizeY;
            int z = idx / (sizeX * sizeY);

            int rx = rotateX(sizeX, sizeZ, x, z, geometryRotation);
            int rz = rotateZ(sizeX, sizeZ, x, z, geometryRotation);

            int newIdx = rx + (y * newSizeX) + (rz * newSizeX * sizeY);
            rotatedBlocks[newIdx] = blocks[idx];
        }
        Jigsaw[] rotatedJigsaws = new Jigsaw[jigsaws.length];
        for (int idx = 0; idx < jigsaws.length; idx++) {
            Jigsaw jigsaw = this.jigsaws[idx];
            int rx = rotateX(sizeX, sizeZ, jigsaw.x, jigsaw.z, geometryRotation);
            int rz = rotateZ(sizeX, sizeZ, jigsaw.x, jigsaw.z, geometryRotation);
            BlockState rotatedFinalState = switch (stateRotation) {
                case ROTATE_90 -> StructureRotationUtil.clockwise90(jigsaw.finalState);
                case ROTATE_180 -> StructureRotationUtil.clockwise180(jigsaw.finalState);
                case ROTATE_270 -> StructureRotationUtil.counterclockwise90(jigsaw.finalState);
                default -> jigsaw.finalState;
            };
            rotatedJigsaws[idx] = new Jigsaw(rx, jigsaw.y, rz, rotatedFinalState, jigsaw.name, jigsaw.joint, jigsaw.pool, jigsaw.target, jigsaw.placementPriority, jigsaw.selectionPriority);
        }

        return new PNXStructure(newSizeX, sizeY, newSizeZ, rotatedPalette, rotatedBlocks, rotatedJigsaws);
    }

    private Rotation inverseRotation(Rotation rotation) {
        return switch (rotation) {
            case ROTATE_90 -> Rotation.ROTATE_270;
            case ROTATE_270 -> Rotation.ROTATE_90;
            default -> rotation;
        };
    }

    @Override
    public PNXStructure mirror(Mirror mirror) {
        if (mirror == Mirror.NONE) return this;

        byte[] mirroredBlocks = new byte[blocks.length];

        for (int idx = 0; idx < blocks.length; idx++) {
            int x = idx % sizeX;
            int y = (idx / sizeX) % sizeY;
            int z = idx / (sizeX * sizeY);

            int mx = x, mz = z;
            switch (mirror) {
                case X -> mx = sizeX - 1 - x;
                case Z -> mz = sizeZ - 1 - z;
                case XZ -> {
                    mx = sizeX - 1 - x;
                    mz = sizeZ - 1 - z;
                }
            }

            int newIdx = mx + (y * sizeX) + (mz * sizeX * sizeY);
            mirroredBlocks[newIdx] = blocks[idx];
        }

        Jigsaw[] mirroredJigsaws = new Jigsaw[jigsaws.length];
        for (int idx = 0; idx < jigsaws.length; idx++) {
            Jigsaw jigsaw = this.jigsaws[idx];
            int mx = jigsaw.x;
            int mz = jigsaw.z;
            switch (mirror) {
                case X -> mx = sizeX - 1 - jigsaw.x;
                case Z -> mz = sizeZ - 1 - jigsaw.z;
                case XZ -> {
                    mx = sizeX - 1 - jigsaw.x;
                    mz = sizeZ - 1 - jigsaw.z;
                }
            }
            mirroredJigsaws[idx] = new Jigsaw(mx, jigsaw.y, mz, jigsaw.finalState, jigsaw.name, jigsaw.joint, jigsaw.pool, jigsaw.target, jigsaw.placementPriority, jigsaw.selectionPriority);
        }

        return new PNXStructure(sizeX, sizeY, sizeZ, palette, mirroredBlocks, mirroredJigsaws);
    }

    @Override
    public NbtMap toNBT() {
        // implement if saving back needed
        return null;
    }

    public static class StructureBlockInstance {
        public final int x, y, z;
        public final BlockState state;

        public StructureBlockInstance(int x, int y, int z, BlockState state) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.state = state;
        }
    }

    private int flattenIndex(int x, int y, int z) {
        return x + (y * sizeX) + (z * sizeX * sizeY);
    }

    private void checkBounds(int x, int y, int z) {
        if (x < 0 || x >= sizeX || y < 0 || y >= sizeY || z < 0 || z >= sizeZ) {
            throw new IndexOutOfBoundsException("Block position out of bounds: " + x + ", " + y + ", " + z);
        }
    }

    private int findPaletteIndex(BlockState state) {
        for (int i = 0; i < palette.length; i++) {
            if (palette[i].equals(state)) {
                return i;
            }
        }
        return -1;
    }

    @Getter
    @AllArgsConstructor
    public static class Jigsaw {
        public int x, y, z;
        public BlockState finalState;
        private String name;
        private String joint;
        private String pool;
        private String target;
        private int placementPriority;
        private int selectionPriority;

        public Jigsaw(NbtMap tag) {
            int[] pos = tag.getIntArray("pos");
            this.x = pos[0];
            this.y = pos[1];
            this.z = pos[2];
            this.finalState = Registries.BLOCKSTATE.get(tag.getInt("final_state"));
            this.name = tag.getString("name");
            this.joint = tag.getString("joint");
            this.pool = tag.getString("pool");
            this.target = tag.getString("target");
            this.placementPriority = tag.containsKey("placement_priority") ? tag.getInt("placement_priority") : 0;
            this.selectionPriority = tag.containsKey("selection_priority") ? tag.getInt("selection_priority") : 0;
        }

        public Jigsaw withPosition(int x, int y, int z) {
            return new Jigsaw(x, y, z, finalState, name, joint, pool, target, placementPriority, selectionPriority);
        }
    }
}
