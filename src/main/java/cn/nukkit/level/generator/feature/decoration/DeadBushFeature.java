package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockDeadbush;
import cn.nukkit.block.BlockState;

public class DeadBushFeature extends GroupedDiscFeature {

    protected final static BlockState DEAD_BUSH = BlockDeadbush.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:dead_bush_feature";

    @Override
    public BlockState getSourceBlock() {
        return DEAD_BUSH;
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
        return 0.2f;
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
