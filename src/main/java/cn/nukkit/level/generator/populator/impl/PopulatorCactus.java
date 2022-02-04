package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.helper.EnsureBelow;
import cn.nukkit.level.generator.populator.helper.EnsureCover;
import cn.nukkit.level.generator.populator.type.PopulatorSurfaceBlock;
import cn.nukkit.math.NukkitRandom;

/**
 * @author DaPorkchop_
 */
public class PopulatorCactus extends PopulatorSurfaceBlock {
    @Override
    protected void populateCount(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        int x = random.nextBoundedInt(16);
        int z = random.nextBoundedInt(16);
        int y = getHighestWorkableBlock(level, x, z, chunk);
        int i1 = random.nextRange(0, 2);
        if (y > 0) {
            for (int i = 0; i < i1; i++) {
                y += i;
                if (canStay(x, y, z, chunk)) {
                    placeBlock(x, y, z, getBlockId(x, z, random, chunk), chunk, random);
                }
            }
        }
    }

    @Override
    protected boolean canStay(int x, int y, int z, FullChunk chunk) {
        return EnsureCover.ensureCover(x, y, z, chunk) && (EnsureBelow.ensureBelow(x, y, z, SAND, chunk) || EnsureBelow.ensureBelow(x, y, z, CACTUS, chunk));
    }

    @Override
    protected int getBlockId(int x, int z, NukkitRandom random, FullChunk chunk) {
        return (CACTUS << Block.DATA_BITS) | 1;
    }
}
