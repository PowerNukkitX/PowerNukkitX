package cn.nukkit.level.generator.populator.impl.nether;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;

import java.util.ArrayList;

public class WarpedTwistingVinesPopulator extends Populator {
    private ChunkManager level;

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        this.level = level;
        int amount = random.nextBoundedInt(5) + 2;

        for (int i = 0; i < amount; ++i) {
            int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
            int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
            ArrayList<Integer> ys = this.getHighestWorkableBlocks(x, z);
            for (int y : ys) {
                if (y <= 1) continue;
                if (random.nextBoundedInt(4) == 0) continue;
                int endY = this.getHighestEndingBlock(x, y, z);
                int amountToDecrease = random.nextBoundedInt(endY - y);
                for (int yPos = y; yPos < y + (amountToDecrease / 2); yPos++) {
                    this.level.setBlockAt(x, yPos, z, TWISTING_VINES);
                }
            }
        }
    }

    private int getHighestEndingBlock(int x, int y, int z) {
        for (; y < 128; ++y) {
            int b = this.level.getBlockIdAt(x, y, z);
            int higherBlockID = this.level.getBlockIdAt(x, y + 1, z);
            if (b == 0 && (
                    higherBlockID == NETHERRACK || higherBlockID == WARPED_NYLIUM || higherBlockID == WARPED_WART_BLOCK ||
                            higherBlockID == STILL_LAVA || higherBlockID == FLOWING_LAVA ||
                            higherBlockID == WARPED_FUNGUS || higherBlockID == WARPED_ROOTS ||
                            higherBlockID == QUARTZ_ORE || higherBlockID == NETHER_GOLD_ORE || higherBlockID == ANCIENT_DERBRIS)) {
                break;
            }
        }

        return y;
    }

    private ArrayList<Integer> getHighestWorkableBlocks(int x, int z) {
        int y;
        ArrayList<Integer> blockYs = new ArrayList<>();
        for (y = 128; y > 0; --y) {
            int b = this.level.getBlockIdAt(x, y, z);
            if ((b == Block.WARPED_NYLIUM || b == Block.NETHERRACK) && this.level.getBlockIdAt(x, y + 1, z) == 0) {
                blockYs.add(y + 1);
            }
        }
        return blockYs;
    }
}
