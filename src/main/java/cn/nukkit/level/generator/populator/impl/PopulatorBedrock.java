package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;

/**
 * @author DaPorkchop_
 * <p>
 * Places bedrock on the bottom of the world
 */
public class PopulatorBedrock extends Populator {

    public static final int BEDROCK_LAYER = -64;
    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunk.setBlockId(x, BEDROCK_LAYER, z, BEDROCK);
                for (int i = 1; i < 5; i++) {
                    if (random.nextBoundedInt(i) == 0) { //decreasing amount
                        chunk.setBlockId(x, BEDROCK_LAYER +i, z, BEDROCK);
                    }
                }
            }
        }
    }
}
