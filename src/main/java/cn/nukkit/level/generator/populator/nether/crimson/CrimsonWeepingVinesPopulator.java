package cn.nukkit.level.generator.populator.nether.crimson;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.Xoroshiro128;

import java.util.ArrayList;
import java.util.Objects;

import static cn.nukkit.block.BlockID.*;

public class CrimsonWeepingVinesPopulator extends Populator {

    public static final String NAME = "nether_crimson_weeping_vines";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        BlockManager object = new BlockManager(level);
        int amount = random.nextInt(5) + 1;

        for (int i = 0; i < amount; ++i) {
            int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
            int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
            if(level.getBiomeId(x, 0, z) != BiomeID.CRIMSON_FOREST) continue;

            ArrayList<Integer> ys = this.getHighestWorkableBlocks(object, x, z);
            for (int y : ys) {
                if (y <= 1) continue;
                int endY = this.getHighestEndingBlock(object, x, y, z);
                int amountToDecrease = random.nextInt(y - endY);
                for (int yPos = y; yPos > y - amountToDecrease; yPos--) {
                    object.setBlockStateAt(x, yPos, z, WEEPING_VINES);
                }
            }
        }
        queueObject(chunk, object);
    }

    private int getHighestEndingBlock(BlockManager level, int x, int y, int z) {
        for (; y > 0; --y) {
            String b = level.getBlockIdIfCachedOrLoaded(x, y, z);
            String higherBlockID = level.getBlockIdIfCachedOrLoaded(x, y + 1, z);
            if (higherBlockID == AIR && (
                    b == NETHERRACK || b == CRIMSON_NYLIUM || b == NETHER_WART_BLOCK ||
                            b == LAVA || b == FLOWING_LAVA ||
                            b == CRIMSON_FUNGUS || b == CRIMSON_ROOTS ||
                            b == QUARTZ_ORE || b == NETHER_GOLD_ORE || b == ANCIENT_DEBRIS)) {
                break;
            }
        }

        return ++y;
    }

    private ArrayList<Integer> getHighestWorkableBlocks(BlockManager level, int x, int z) {
        int y;
        ArrayList<Integer> blockYs = new ArrayList<>();
        for (y = 128; y > 0; --y) {
            String b = level.getBlockIdIfCachedOrLoaded(x, y, z);
            if ((b == Block.CRIMSON_NYLIUM || b == Block.NETHERRACK) && level.getBlockIfCachedOrLoaded(x, y - 1, z).isAir()) {
                blockYs.add(y - 1);
            }
        }
        return blockYs;
    }

    @Override
    public String name() {
        return NAME;
    }
}
