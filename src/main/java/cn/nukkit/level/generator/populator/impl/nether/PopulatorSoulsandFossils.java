package cn.nukkit.level.generator.populator.impl.nether;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;

public class PopulatorSoulsandFossils extends Populator {
    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        if(random.nextBoundedInt(5) == 0) {
            int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
            int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
            int y = this.getHighestWorkableBlock(chunk, x & 0xF, z & 0xF);
            if (y != -1 && level.getBlockIdAt(x, y, z) != NETHERRACK) {
                int count = NukkitMath.randomRange(random, 10, 20);
                for (int i = 0; i < count; i++) {
                    level.setBlockAt(x + (random.nextBoundedInt(6) - 3), y + (random.nextBoundedInt(3)), z + (random.nextBoundedInt(6) - 3), BONE_BLOCK);
                }
            }
        }
    }

    private int getHighestWorkableBlock(FullChunk chunk, int x, int z) {
        int y;
        //start scanning a bit lower down to allow space for placing on top
        for (y = 120; y >= 0; y--) {
            int b = chunk.getBlockId(x, y, z);
            if (b == SOUL_SAND || b == SOUL_SOIL) {
                break;
            }
        }
        return y == 0 ? -1 : y;
    }
}
