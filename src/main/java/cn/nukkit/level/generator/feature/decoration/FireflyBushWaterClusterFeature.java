package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockFireflyBush;
import cn.nukkit.block.BlockReeds;
import cn.nukkit.block.BlockState;
import cn.nukkit.tags.BlockTags;

public class FireflyBushWaterClusterFeature extends GroupedDiscFeature {

    protected final static BlockState FIREFLY_BUSH = BlockFireflyBush.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:firefly_bush_water_cluster_feature";

    @Override
    public BlockState getSourceBlock() {
        return FIREFLY_BUSH;
    }

    @Override
    public int getMinRadius() {
        return 3;
    }

    @Override
    public int getMaxRadius() {
        return 4;
    }

    @Override
    public double getProbability() {
        return 0.5f;
    }

    @Override
    public int getBase() {
        return -10;
    }

    @Override
    public int getRandom() {
        return 12;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public boolean isSupportValid(Block block) {
        return block.hasTag(BlockTags.DIRT) && BlockReeds.isSupportValid(block);
    }
}
