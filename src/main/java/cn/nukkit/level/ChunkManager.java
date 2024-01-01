package cn.nukkit.level;

import cn.nukkit.block.BlockState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.ChunkVector2;
import org.jetbrains.annotations.NotNull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public interface ChunkManager {
    void setBlockStateAt(int x, int y, int z, int layer, BlockState state);

    default void setBlockStateAt(int x, int y, int z, BlockState state) {
        setBlockStateAt(x, y, z, 0, state);
    }

    BlockState getBlockStateAt(int x, int y, int z, int layer);

    default BlockState getBlockStateAt(int x, int y, int z) {
        return getBlockStateAt(x, y, z, 0);
    }

    IChunk getChunk(int chunkX, int chunkZ);

    default IChunk getChunk(@NotNull ChunkVector2 pos) {
        return getChunk(pos.getX(), pos.getZ());
    }

    void setChunk(int chunkX, int chunkZ);

    void setChunk(int chunkX, int chunkZ, IChunk chunk);

    long getSeed();

    void setSeed(long seed);

    boolean isOverWorld();

    boolean isNether();

    boolean isTheEnd();

    void cleanChunks(long seed);

    static int ensureY(final int y, final IChunk chunk) {
        return Math.max(Math.min(y, chunk.getDimensionData().getMaxHeight()), chunk.getDimensionData().getMinHeight());
    }
}
