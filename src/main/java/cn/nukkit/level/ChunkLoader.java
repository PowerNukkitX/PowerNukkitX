package cn.nukkit.level;

import cn.nukkit.level.format.IChunk;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public interface ChunkLoader {


    ChunkLoader[] EMPTY_ARRAY = new ChunkLoader[0];

    int getLoaderId();

    boolean isLoaderActive();

    Position getPosition();

    double getX();

    double getZ();

    Level getLevel();

    void onChunkChanged(IChunk chunk);

    void onChunkLoaded(IChunk chunk);

    void onChunkUnloaded(IChunk chunk);
}
