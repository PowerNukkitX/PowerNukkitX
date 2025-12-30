package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockBlueOrchid;
import cn.nukkit.block.BlockDeadbush;
import cn.nukkit.block.BlockState;

public class SwampFlowerFeature extends GroupedDiscFeature {

    protected final static BlockState STATE = BlockBlueOrchid.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:scatter_swamp_flower_feature";

    @Override
    public BlockState getSourceBlock() {
        return STATE;
    }

    @Override
    public int getMinRadius() {
        return 1;
    }

    @Override
    public int getMaxRadius() {
        return 2;
    }

    @Override
    public double getProbability() {
        return 0.7f;
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
