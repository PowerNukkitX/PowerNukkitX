package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.BlockKelp;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.Normal;
import cn.nukkit.level.generator.populator.helper.PopulatorHelpers;
import cn.nukkit.level.generator.populator.type.PopulatorOceanFloorSurfaceBlock;
import cn.nukkit.math.NukkitRandom;

/**
 * @author GoodLucky777
 */
public class PopulatorKelp extends PopulatorOceanFloorSurfaceBlock {

    private static final BlockState STATE_STILL_WATER = BlockState.of(STILL_WATER);
    private static final BlockState STATE_KELP = BlockState.of(BLOCK_KELP);
    private static final BlockState STATE_KELP_AGE_MAX = BlockState.of(BLOCK_KELP, BlockKelp.KELP_AGE.getMaxValue());
    
    @Override
    public void populateCount(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        // TODO: Use Noise
        int x = random.nextBoundedInt(16);
        int z = random.nextBoundedInt(16);
        int y = getHighestWorkableBlock(level, x, z, chunk);
        
        if (y > 0) {
            if (!canStay(x, y + 1, z, chunk)) {
                return;
            }
            
            if (!chunk.getBlockState(x, y - 1, z).getBlock().isSolid()) {
                if (chunk.getBlockId(x, y - 1, z) != BLOCK_KELP) {
                    return;
                }
            }
            
            int height = random.nextBoundedInt(10) + 1;
            for (int h = 0; h <= height; h++) {
                if (canStay(x, y + h, z, chunk)) {
                    if (h == height || !chunk.getBlockState(x, y + h + 2, z).equals(STATE_STILL_WATER)) {
                        chunk.setBlockState(x, y + h, z, STATE_KELP.withData(20 + random.nextBoundedInt(4)));
                        chunk.setBlockStateAtLayer(x, y + h, z, 1, STATE_STILL_WATER);
                        return;
                    } else {
                        chunk.setBlockState(x, y + h, z, STATE_KELP_AGE_MAX);
                        chunk.setBlockStateAtLayer(x, y + h, z, 1, STATE_STILL_WATER);
                    }
                } else {
                    return;
                }
            }
        }
    }
    
    @Override
    protected boolean canStay(int x, int y, int z, FullChunk chunk) {
        return chunk.getBlockState(x, y, z).equals(STATE_STILL_WATER);
    }
    
    @Override
    protected BlockState getBlockState(int x, int z, NukkitRandom random, FullChunk chunk) {
        return STATE_KELP;
    }
}
