package cn.nukkit.level.structure;

import cn.nukkit.block.BlockState;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.types.StructureMirror;
import cn.nukkit.network.protocol.types.StructureRotation;
import cn.nukkit.registry.Registries;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
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
    private final BlockState[] palette;
    private final byte[] blocks; // indices into palette (+1), 0 = air

    private PNXStructure(int sizeX, int sizeY, int sizeZ, BlockState[] palette, byte[] blocks) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.palette = palette;
        this.blocks = blocks;
    }

    public static PNXStructure fromNbt(CompoundTag nbt) {
        int[] sizeNbt = nbt.getIntArray("size");
        int sizeX = sizeNbt.length > 0 ? sizeNbt[0] : 0;
        int sizeY = sizeNbt.length > 1 ? sizeNbt[1] : 0;
        int sizeZ = sizeNbt.length > 2 ? sizeNbt[2] : 0;

        // --- palette (direct BlockState array) ---
        ListTag<IntTag> paletteNbt = nbt.getList("palette", IntTag.class);
        BlockState[] palette = new BlockState[paletteNbt.size()];
        for (int i = 0; i < paletteNbt.size(); i++) {
            int hash = paletteNbt.get(i).data;
            BlockState state = Registries.BLOCKSTATE.get(hash);
            if (state == null) {
                log.warn("Unknown block state hash in structure palette: {}", hash);
                state = STATE_UNKNOWN;
            }
            palette[i] = state;
        }

        // --- blocks (raw indices) ---
        byte[] blocks = nbt.getByteArray("blocks");

        return new PNXStructure(sizeX, sizeY, sizeZ, palette, blocks);
    }

    public static CompletableFuture<PNXStructure> fromNbtAsync(CompoundTag nbt) {
        return CompletableFuture.supplyAsync(() -> fromNbt(nbt));
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
                    state = STATE_AIR;
                } else if (paletteIndex < 0 || paletteIndex >= palette.length) {
                    state = STATE_UNKNOWN;
                } else {
                    state = palette[paletteIndex];
                }

                return new StructureBlockInstance(x, y, z, state);
            }
        };
    }

    public void preparePlace(Position position, BlockManager blockManager) {
        int baseX = position.getFloorX();
        int baseY = position.getFloorY();
        int baseZ = position.getFloorZ();

        for (StructureBlockInstance b : getBlockInstances()) {
            blockManager.setBlockStateAt(baseX + b.x, baseY + b.y, baseZ + b.z, b.state);
        }
    }

    public void place(Position position, boolean includeEntities, BlockManager blockManager) {
        preparePlace(position, blockManager);
        blockManager.applySubChunkUpdate();
    }

    public PNXStructure rotate(StructureRotation rotation) {
        // Rotation support could also be done lazily, but here we materialize.
        if (rotation == StructureRotation.NONE) return this;

        int newSizeX = (rotation == StructureRotation.ROTATE_180) ? sizeX : sizeZ;
        int newSizeZ = (rotation == StructureRotation.ROTATE_180) ? sizeZ : sizeX;

        byte[] rotatedBlocks = new byte[blocks.length];

        for (int idx = 0; idx < blocks.length; idx++) {
            int x = idx % sizeX;
            int y = (idx / sizeX) % sizeY;
            int z = idx / (sizeX * sizeY);

            int rx = x, rz = z;
            switch (rotation) {
                case ROTATE_90 -> { rx = z; rz = sizeX - 1 - x; }
                case ROTATE_180 -> { rx = sizeX - 1 - x; rz = sizeZ - 1 - z; }
                case ROTATE_270 -> { rx = sizeZ - 1 - z; rz = x; }
            }

            int newIdx = rx + (y * newSizeX) + (rz * newSizeX * sizeY);
            rotatedBlocks[newIdx] = blocks[idx];
        }

        return new PNXStructure(newSizeX, sizeY, newSizeZ, palette, rotatedBlocks);
    }

    public PNXStructure mirror(StructureMirror mirror) {
        if (mirror == StructureMirror.NONE) return this;

        byte[] mirroredBlocks = new byte[blocks.length];

        for (int idx = 0; idx < blocks.length; idx++) {
            int x = idx % sizeX;
            int y = (idx / sizeX) % sizeY;
            int z = idx / (sizeX * sizeY);

            int mx = x, mz = z;
            switch (mirror) {
                case X -> mx = sizeX - 1 - x;
                case Z -> mz = sizeZ - 1 - z;
                case XZ -> { mx = sizeX - 1 - x; mz = sizeZ - 1 - z; }
            }

            int newIdx = mx + (y * sizeX) + (mz * sizeX * sizeY);
            mirroredBlocks[newIdx] = blocks[idx];
        }

        return new PNXStructure(sizeX, sizeY, sizeZ, palette, mirroredBlocks);
    }

    @Override
    public CompoundTag toNBT() {
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
}