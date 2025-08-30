package cn.nukkit.level.generator.feature.foliage;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockReeds;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.feature.CountGenerateFeature;
import cn.nukkit.tags.BlockTags;
import cn.nukkit.utils.random.NukkitRandom;

public class SugarcaneFeature extends CountGenerateFeature {

    public static final String NAME = "minecraft:overworld_after_surface_reeds_feature_rules";

    private boolean findWater(int x, int y, int z, IChunk chunk) {
        for (int i = x - 1; i < (x + 1); i++) {
            for (int j = z - 1; j < (z + 1); j++) {
                String b = chunk.getBlockState(i, y, j).getIdentifier();
                if (b == Block.FLOWING_WATER || b == Block.WATER) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getBase() {
        return 0;
    }

    @Override
    public int getRandom() {
        return 3;
    }

    @Override
    public void populate(ChunkGenerateContext context, NukkitRandom random) {
        IChunk chunk = context.getChunk();
        int x = random.nextBoundedInt(15);
        int z = random.nextBoundedInt(15);
        int y = context.getChunk().getHeightMap(x, z);
        if (y > 0 && canStay(x, y, z, chunk)) {
            for(int i = 0; i < random.nextInt(1, 4); i++) {
                chunk.setBlockState(x, y+i, z, BlockReeds.PROPERTIES.getDefaultState());
            }
        }
    }

    protected boolean canStay(int x, int y, int z, IChunk chunk) {
        try {
            return chunk.getBlockState(x, y, z).toBlock().isAir() && chunk.getBlockState(x, y-1, z).toBlock().is(BlockTags.SAND) && findWater(x, y - 1, z, chunk);
        } catch (Exception e) {
            return false;
        }
    }
    @Override
    public String name() {
        return NAME;
    }
}
