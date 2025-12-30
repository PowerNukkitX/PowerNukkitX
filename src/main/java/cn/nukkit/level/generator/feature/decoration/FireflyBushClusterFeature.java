package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockFireflyBush;
import cn.nukkit.block.BlockState;

public class FireflyBushClusterFeature extends GroupedDiscFeature {

    protected final static BlockState SOURCE = BlockFireflyBush.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:firefly_bush_cluster_feature";

    @Override
    public BlockState getSourceBlock() {
        return SOURCE;
    }

    @Override
    public int getMinRadius() {
        return 1;
    }

    @Override
    public int getMaxRadius() {
        return 1;
    }

    @Override
    public double getProbability() {
        return 0.8f;
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
}
