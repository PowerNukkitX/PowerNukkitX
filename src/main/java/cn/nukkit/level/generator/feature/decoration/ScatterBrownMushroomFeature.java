package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.BlockBrownMushroom;
import cn.nukkit.block.BlockState;

public class ScatterBrownMushroomFeature extends ScatterRedMushroomFeature {

    private static final BlockState STATE = BlockBrownMushroom.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:scatter_brown_mushroom_feature";

    @Override
    public BlockState getSourceBlock() {
        return STATE;
    }

    @Override
    public int getBase() {
        return -3;
    }

    @Override
    public int getRandom() {
        return 4;
    }

    @Override
    public String name() {
        return NAME;
    }
}
