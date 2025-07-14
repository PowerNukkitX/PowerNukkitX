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

        int minY = chunk.getProvider().getDimensionData().getMinHeight();
        int maxY = chunk.getProvider().getDimensionData().getMaxHeight();

        var blockData = cloneBlockData(chunk, minY, maxY);
        var blockEntities = cloneBlockEntities(chunk);

        applyBlockData(chunk, blockData, minY, maxY);
        restoreBlockEntities(chunk, blockEntities);

        chunk.recalculateHeightMap();
        chunk.populateSkyLight();
        chunk.setLightPopulated();
        chunk.hasChanged();
        chunk.setChunkState(ChunkState.FINISHED);

        log.debug("[ChunkConversion] Chunk at ({}, {}) converted successfully.", chunk.getX(), chunk.getZ());
        return chunk;
    }

    private static BlockData cloneBlockData(IChunk chunk, int minY, int maxY) {
        int height = maxY - minY;
        BlockState[][][] blocks = new BlockState[16][height][16];
        int[][][] biomes = new int[16][height][16];
        int[][] heightMap = new int[16][16];

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
        return new BlockData(blocks, biomes, heightMap);
    }

    private static List<CompoundTag> cloneBlockEntities(IChunk chunk) {
        List<CompoundTag> blockEntities = new ArrayList<>();
        for (BlockEntity be : chunk.getBlockEntities().values()) {
            blockEntities.add(be.namedTag.copy());
        }
        return blockEntities;
    }

    private static void applyBlockData(IChunk chunk, BlockData data, int minY, int maxY) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    int ny = y - minY;
                    chunk.setBlockState(x, y, z, data.blocks[x][ny][z]);
                    chunk.setBiomeId(x, y, z, data.biomes[x][ny][z]);
                }
                chunk.setHeightMap(x, z, data.heightMap[x][z]);
            }
        }
    }

    private static void restoreBlockEntities(IChunk chunk, List<CompoundTag> tags) {
        chunk.getBlockEntities().clear();
        for (CompoundTag tag : tags) {
            BlockEntity be = BlockEntity.createBlockEntity(tag.getString("id"), chunk, tag);
            if (be != null) {
                chunk.addBlockEntity(be);
            }
        }
    }

    private record BlockData(BlockState[][][] blocks, int[][][] biomes, int[][] heightMap) {}
}
