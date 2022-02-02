package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.BlockVine;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.PopulatorHelpers;
import cn.nukkit.level.generator.populator.type.PopulatorCount;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;

import java.util.HashSet;
import java.util.Set;

/**
 * @author GoodLucky777
 */
public class PopulatorVines extends PopulatorCount {

    private void generateVines(ChunkManager level, int x, int z, NukkitRandom random) {
        for (int y = 64; y < 256; y++) {
            // Randomize x, z
            x += random.nextBoundedInt(4) - random.nextBoundedInt(4);
            z += random.nextBoundedInt(4) - random.nextBoundedInt(4);
            
            if (level.getBlockIdAt(x, y, z) == AIR) {
                Set<BlockFace> attachFaces = new HashSet<>();
                
                if (level.getBlockStateAt(x - 1, y, z).getBlock().isSolid()) {
                    attachFaces.add(BlockFace.WEST);
                }
                
                if (level.getBlockStateAt(x + 1, y, z).getBlock().isSolid()) {
                    attachFaces.add(BlockFace.EAST);
                }
                
                if (level.getBlockStateAt(x, y, z - 1).getBlock().isSolid()) {
                    attachFaces.add(BlockFace.SOUTH);
                }
                
                if (level.getBlockStateAt(x, y, z + 1).getBlock().isSolid()) {
                    attachFaces.add(BlockFace.NORTH);
                }
                
                if (!attachFaces.isEmpty()) {
                    level.setBlockStateAt(x, y, z, BlockState.of(VINE, BlockVine.getMetaFromFaces(attachFaces)));
                }
            }
        }
    }
    
    @Override
    protected int getHighestWorkableBlock(ChunkManager level, int x, int z, FullChunk chunk) {
        return -1;
    }
    
    @Override
    protected void populateCount(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        generateVines(level, (chunkX << 4) + random.nextBoundedInt(16), (chunkZ << 4) + random.nextBoundedInt(16), random);
    }
}
