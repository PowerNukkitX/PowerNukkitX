package cn.nukkit.level.generator;

import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.IChunk;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class SimpleChunkManager implements ChunkManager {

    protected long seed;

    public SimpleChunkManager(long seed) {
        this.seed = seed;
    }

    @Override
    public BlockState getBlockStateAt(int x, int y, int z, int layer) {
        IChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            return chunk.getBlockState(x & 0xf, ChunkManager.ensureY(y, chunk), z & 0xf, layer);
        }
        return BlockAir.PROPERTIES.getDefaultState();
    }

    @Override
    public void setBlockStateAt(int x, int y, int z, BlockState state) {
        IChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.setBlockState(x & 0xf, ChunkManager.ensureY(y, chunk), z & 0xf, state);
        }
    }

    @Override
    public void setBlockStateAt(int x, int y, int z, int layer, BlockState state) {
        IChunk chunk = this.getChunk(x >> 4, z >> 4);
        if (chunk != null) {
            chunk.setBlockState(x & 0xf, ChunkManager.ensureY(y, chunk), z & 0xf, state, layer);
        }
    }

    @Override
    public void setChunk(int chunkX, int chunkZ) {
        this.setChunk(chunkX, chunkZ, null);
    }

    @Override
    public long getSeed() {
        return seed;
    }

    @Override
    public void setSeed(long seed) {
        this.seed = seed;
    }

    @Override
    public void cleanChunks(long seed) {
        this.seed = seed;
    }
}
