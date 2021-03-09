package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.Normal;
import cn.nukkit.level.generator.populator.helper.PopulatorHelpers;
import cn.nukkit.level.generator.populator.type.PopulatorCount;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;

import java.util.Arrays;
import java.util.List;

/**
 * @author GoodLucky777
 */
public class PopulatorDisk extends PopulatorCount {

    private static final BlockState STATE_STILL_WATER = BlockState.of(STILL_WATER);
    
    private double probability;
    private BlockState sourceBlock;
    private int radiusMin;
    private int radiusMax;
    private int radiusY;
    private List<BlockState> replaceBlocks;
    
    public PopulatorDisk() {
        this.probability = 1.0;
        this.sourceBlock = BlockState.of(GRAVEL);
        this.radiusMin = 2;
        this.radiusMax = 3;
        this.radiusY = 2;
        this.replaceBlocks = Arrays.asList(BlockState.of(DIRT), BlockState.of(GRASS));
    }
    
    public PopulatorDisk(double probability, BlockState sourceBlock, int radiusMin, int radiusMax, int radiusY, List<BlockState> replaceBlocks) {
        this.probability = probability;
        this.sourceBlock = sourceBlock;
        this.radiusMin = radiusMin;
        this.radiusMax = radiusMax;
        this.radiusY = radiusY;
        this.replaceBlocks = replaceBlocks;
    }
    
    @Override
    public void populateCount(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        if (random.nextDouble() >= probability) {
            return;
        }
        
        int sourceX = (chunkX << 4) + random.nextBoundedInt(16);
        int sourceZ = (chunkZ << 4) + random.nextBoundedInt(16);
        int sourceY = getHighestWorkableBlock(level, sourceX, sourceZ, chunk) - 1;
        if (sourceY < radiusY) {
            return;
        }
        
        if (!level.getBlockStateAt(sourceX, sourceY + 1, sourceZ).equals(STATE_STILL_WATER)) {
            return;
        }
        
        int radius = NukkitMath.randomRange(random, radiusMin, radiusMax);
        for (int x = sourceX - radius; x <= sourceX + radius; x++) {
            for (int z = sourceZ - radius; z <= sourceZ + radius; z++) {
                if ((x - sourceX) * (x - sourceX) + (z - sourceZ) * (z - sourceZ) <= radius * radius) {
                    for (int y = sourceY - radiusY; y <= sourceY + radiusY; y++) {
                        for (BlockState replaceBlockState : replaceBlocks) {
                            if (level.getBlockStateAt(x, y, z).equals(replaceBlockState)) {
                                level.setBlockStateAt(x, y, z, sourceBlock);
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    protected int getHighestWorkableBlock(ChunkManager level, int x, int z, FullChunk chunk) {
        int y;
        for (y = Normal.seaHeight - 1; y >= 0; --y) {
            if (!PopulatorHelpers.isNonOceanSolid(level.getBlockStateAt(x, y, z))) {
                break;
            }
        }
        
        return y == 0 ? -1 : ++y;
    }
}
