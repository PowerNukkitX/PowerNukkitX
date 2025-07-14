package cn.nukkit.level.format;

import cn.nukkit.block.BlockState;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class ChunkConversion {

    public static IChunk convert(IChunk chunk) {
        log.debug("[ChunkConversion] Converting chunk at ({}, {})...", chunk.getX(), chunk.getZ());

        // Fetch height range
        int minY = chunk.getProvider().getDimensionData().getMinHeight();
        int maxY = chunk.getProvider().getDimensionData().getMaxHeight();
        int height = maxY - minY;

        // Clone data
        BlockState[][][] blocks = new BlockState[16][height][16];
        int[][][] biomes = new int[16][height][16];
        int[][] heightMap = new int[16][16];
        List<CompoundTag> blockEntities = new ArrayList<>();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    int ny = y - minY;
                    blocks[x][ny][z] = chunk.getBlockState(x, y, z);
                    biomes[x][ny][z] = chunk.getBiomeId(x, y, z);
                }
                heightMap[x][z] = chunk.getHeightMap(x, z);
            }
        }

        for (BlockEntity be : chunk.getBlockEntities().values()) {
            blockEntities.add(be.namedTag.copy());
        }

        // Rebuild chunk with copied data
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    int ny = y - minY;
                    chunk.setBlockState(x, y, z, blocks[x][ny][z]);
                    chunk.setBiomeId(x, y, z, biomes[x][ny][z]);
                }
                chunk.setHeightMap(x, z, heightMap[x][z]);
            }
        }

        // Restore block entities
        chunk.getBlockEntities().clear();
        for (CompoundTag tag : blockEntities) {
            String type = tag.getString("id");
            BlockEntity be = BlockEntity.createBlockEntity(type, chunk, tag);
            if (be != null) {
                chunk.addBlockEntity(be);
            }
        }

        // Finalize chunk
        chunk.recalculateHeightMap();
        chunk.populateSkyLight();
        chunk.setLightPopulated();
        chunk.hasChanged();
        chunk.setChunkState(ChunkState.FINISHED);

        log.debug("[ChunkConversion] Chunk at ({}, {}) converted successfully.", chunk.getX(), chunk.getZ());
        return chunk;
    }
}
