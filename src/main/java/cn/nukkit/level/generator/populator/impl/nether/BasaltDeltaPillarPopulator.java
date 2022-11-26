package cn.nukkit.level.generator.populator.impl.nether;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.NukkitRandom;

import java.util.ArrayList;

public class BasaltDeltaPillarPopulator extends Populator {
    private ChunkManager level;

    //Clumped together
    //Fairly high, maybe 6 blocks max?
    //They can be sort of ordered, so an empty square basically
    //Lots of lava
    //Less of magma

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        this.level = level;
        int amount = random.nextBoundedInt(128) + 128;

        for (int i = 0; i < amount; ++i) {
            int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
            int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
            ArrayList<Integer> ys = this.getHighestWorkableBlocks(x, z);
            for (int y : ys) {
                if (y <= 1) continue;
                if (random.nextBoundedInt(5) == 0) continue;
                for (int randomHeight = 0; randomHeight < random.nextBoundedInt(5) + 1; randomHeight++) {
                    int placeLocation = y + randomHeight;
                    this.level.setBlockAt(x, placeLocation, z, BlockID.BASALT);
                }
            }
        }
    }

    private ArrayList<Integer> getHighestWorkableBlocks(int x, int z) {
        int y;
        ArrayList<Integer> blockYs = new ArrayList<>();
        for (y = 128; y > 0; --y) {
            int b = this.level.getBlockIdAt(x, y, z);
            if ((b == Block.BASALT || b == Block.BLACKSTONE) && this.level.getBlockIdAt(x, y + 1, z) == 0) {
                blockYs.add(y + 1);
            }
        }
        return blockYs;
    }
}
