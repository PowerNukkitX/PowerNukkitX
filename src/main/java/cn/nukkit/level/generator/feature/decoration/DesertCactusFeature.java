package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockCactus;
import cn.nukkit.block.BlockCactusFlower;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.feature.CountGenerateFeature;
import cn.nukkit.tags.BlockTags;
import cn.nukkit.utils.random.NukkitRandom;

public class DesertCactusFeature extends CountGenerateFeature {

    protected final static BlockState CACTUS = BlockCactus.PROPERTIES.getDefaultState();
    protected final static BlockState CACTUSFLOWER = BlockCactusFlower.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:desert_after_surface_cactus_feature_rules";


    @Override
    public int getBase() {
        return 2;
    }

    @Override
    public int getRandom() {
        return 0;
    }

    @Override
    public void populate(ChunkGenerateContext context, NukkitRandom random) {
        IChunk chunk = context.getChunk();
        int x = random.nextBoundedInt(14) + 1;
        int z = random.nextBoundedInt(14) + 1;
        int y = chunk.getHeightMap(x, z) + 1;
        int height = 1;
        int range = random.nextBoundedInt(18);
        if (range >= 16) {
            height = 3;
        } else if (range >= 11) {
            height = 2;
        }
        if (chunk.getBlockState(x, y-1, z).toBlock().is(BlockTags.SAND) && chunk.getLevel().getBlock(x, y-1, z).getLevelBlockAround().stream().allMatch(Block::isAir)) {
            if (y > 0) {
                for (int i = 0; i <= height; i++) {
                    chunk.setBlockState(x, y+i, z, CACTUS);
                }
                if(random.nextBoolean()) chunk.setBlockState(x, y+height+1, z, CACTUSFLOWER);
                chunk.recalculateHeightMapColumn(x, z);
            }
        }
    }

    @Override
    public String name() {
        return NAME;
    }
}
