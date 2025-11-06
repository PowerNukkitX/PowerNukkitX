package cn.nukkit.level.generator.populator.nether.warped;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.generator.populator.Populator;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.random.Xoroshiro128;

import java.util.ArrayList;
import java.util.Objects;

import static cn.nukkit.block.BlockID.*;
import static cn.nukkit.block.BlockID.ANCIENT_DEBRIS;
import static cn.nukkit.block.BlockID.CRIMSON_ROOTS;
import static cn.nukkit.block.BlockID.NETHER_GOLD_ORE;
import static cn.nukkit.block.BlockID.QUARTZ_ORE;

public class WarpedTwistingVinesPopulator extends Populator {

    public static final String NAME = "nether_warped_twisting_vines";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        Level level = chunk.getLevel();
        random.setSeed(level.getSeed() ^ Level.chunkHash(chunkX, chunkZ));
        BlockManager object = new BlockManager(level);
        int amount = random.nextInt(6) + 2;

        for (int i = 0; i < amount; ++i) {
            int x = NukkitMath.randomRange(random, chunkX << 4, (chunkX << 4) + 15);
            int z = NukkitMath.randomRange(random, chunkZ << 4, (chunkZ << 4) + 15);
            if(level.getBiomeId(x, 0, z) != BiomeID.WARPED_FOREST) continue;
            ArrayList<Integer> ys = this.getHighestWorkableBlocks(object, x, z);
            for (int y : ys) {
                if (y <= 1) continue;
                if (random.nextInt(5) == 0) continue;
                int endY = this.getHighestEndingBlock(object, x, y, z);
                int amountToDecrease = random.nextInt(endY - y + 1);
                for (int yPos = y; yPos < y + (amountToDecrease / 2); yPos++) {
                    object.setBlockStateAt(x, yPos, z, TWISTING_VINES);
                }
            }
        }
        queueObject(chunk, object);
    }


    private int getHighestEndingBlock(BlockManager level, int x, int y, int z) {
        for (; y < 128; ++y) {
            Block b = level.getBlockIfCachedOrLoaded(x, y, z);
            String higherBlockID = level.getBlockIdIfCachedOrLoaded(x, y - 1, z);
            if (Objects.equals(higherBlockID, AIR) && b.isSolid()) {
                break;
            }
        }

        return --y;
    }

    private ArrayList<Integer> getHighestWorkableBlocks(BlockManager level, int x, int z) {
        int y;
        ArrayList<Integer> blockYs = new ArrayList<>();
        for (y = 128; y > 0; --y) {
            String b = level.getBlockIdIfCachedOrLoaded(x, y, z);
            if ((b == WARPED_NYLIUM || b == WARPED_WART_BLOCK) && level.getBlockIfCachedOrLoaded(x, y + 1, z).canBeReplaced()) {
                blockYs.add(y+1);
            }
        }
        return blockYs;
    }

    @Override
    public String name() {
        return NAME;
    }
}
