package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.PopulatorHelpers;
import cn.nukkit.level.generator.populator.type.PopulatorOceanFloorSurfaceBlock;
import cn.nukkit.math.NukkitRandom;

/**
 * @author GoodLucky777
 */
public class PopulatorSeagrass extends PopulatorOceanFloorSurfaceBlock {

    private final static BlockState STATE_STILL_WATER = BlockState.of(STILL_WATER);
    private final static BlockState STATE_SEAGRASS = BlockState.of(SEAGRASS);
    private final static BlockState STATE_TALL_SEAGRASS_TOP = BlockState.of(SEAGRASS, 1);
    private final static BlockState STATE_TALL_SEAGRASS_BOT = BlockState.of(SEAGRASS, 2);
    
    private final double tallSeagrassProbability;
    
    public PopulatorSeagrass() {
        tallSeagrassProbability = 0.3;
    }
    
    public PopulatorSeagrass(double tallSeagrassProbability) {
        this.tallSeagrassProbability = tallSeagrassProbability;
    }
    
    @Override
    public void populateCount(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        int x = random.nextBoundedInt(16);
        int z = random.nextBoundedInt(16);
        int y = getHighestWorkableBlock(level, x, z, chunk);
        if (y > 0 && canStay(x, y, z, chunk)) {
            if (random.nextDouble() < this.tallSeagrassProbability) {
                if (canStay(x, y + 1, z, chunk, true)) {
                    chunk.setBlockState(x, y, z, STATE_TALL_SEAGRASS_BOT);
                    chunk.setBlockStateAtLayer(x, y, z, 1, STATE_STILL_WATER);
                    chunk.setBlockState(x, y + 1, z, STATE_TALL_SEAGRASS_TOP);
                    chunk.setBlockStateAtLayer(x, y + 1, z, 1, STATE_STILL_WATER);
                }
            } else {
                chunk.setBlockState(x, y, z, STATE_SEAGRASS);
                chunk.setBlockStateAtLayer(x, y, z, 1, STATE_STILL_WATER);
            }
        }
    }
    
    @Override
    protected boolean canStay(int x, int y, int z, FullChunk chunk) {
        return canStay(x, y, z, chunk, false);
    }
    
    protected boolean canStay(int x, int y, int z, FullChunk chunk, boolean tallSeagrass) {
        if (tallSeagrass) {
            return chunk.getBlockState(x, y, z).equals(STATE_STILL_WATER);
        } else {
            return chunk.getBlockState(x, y, z).equals(STATE_STILL_WATER) && chunk.getBlockState(x, y - 1, z).getBlock().isSolid();
        }
    }
    
    @Override
    protected BlockState getBlockState(int x, int z, NukkitRandom random, FullChunk chunk) {
        return STATE_SEAGRASS;
    }
}
