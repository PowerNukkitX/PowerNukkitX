package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;

/**
 * @author DaPorkchop_
 * <p>
 * Places bedrock on the bottom of the world
 */
public class PopulatorDeepslate extends Populator {

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for(int y = PopulatorBedrock.BEDROCK_LAYER; y < 0; y++) {
                    if(chunk.getBlockId(x,y,z) == 0) chunk.setBlockId(x, y, z, DEEPSLATE);
                }
                for (int y = 0; y < 8; y++) {
                    if (random.nextBoundedInt(y) == 0) {
                        chunk.setBlockId(x, y, z, DEEPSLATE);
                    }
                }
            }
        }
    }
}
