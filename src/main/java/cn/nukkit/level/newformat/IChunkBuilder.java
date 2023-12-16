package cn.nukkit.level.newformat;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.DimensionData;

import java.util.Map;

/**
 * Allay Project 12/16/2023
 *
 * @author Cool_Loong
 */
public interface IChunkBuilder {
    IChunkBuilder chunkX(int chunkX);

    int getChunkX();

    IChunkBuilder chunkZ(int chunkZ);

    int getChunkZ();

    IChunkBuilder state(ChunkState state);

    IChunkBuilder levelProvider(LevelProvider levelProvider);

    DimensionData getDimensionData();

    IChunkBuilder sections(ChunkSection[] sections);

    IChunkBuilder heightMap(short[] heightMap);

    IChunkBuilder entities(Map<Long, Entity> entities);

    IChunkBuilder blockEntities(Map<Long, BlockEntity> blockEntities);

    IChunk build();

    IChunk emptyChunk(int chunkX, int chunkZ);
}
