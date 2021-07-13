package cn.nukkit.level.generator.populator.impl.nether;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.object.tree.ObjectCrimsonTree;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;

public class CrimsonGrassesPopulator extends Populator {

    private ChunkManager level;

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        this.level = level;
        int amount = random.nextBoundedInt(128) + 128;

        for (int i = 0; i < amount; ++i) {
            int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
            int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
            int y = this.getHighestWorkableBlock(x, z);
            if (y == -1) {
                continue;
            }
            int randomType = random.nextBoundedInt(6);
            int blockID;

            if (randomType == 0) blockID = CRIMSON_FUNGUS;
            else blockID = CRIMSON_ROOTS;

            this.level.setBlockAt(x, y, z, blockID);
        }
    }

    private int getHighestWorkableBlock(int x, int z) {
        int y;
        for (y = 128; y > 0; --y) {
            int b = this.level.getBlockIdAt(x, y, z);
            if (b == Block.CRIMSON_NYLIUM && this.level.getBlockIdAt(x, y+1, z) == 0) {
                break;
            }
        }

        return ++y;
    }
}
