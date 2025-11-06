package cn.nukkit.level.generator.feature.ore;

import cn.nukkit.block.BlockGravel;
import cn.nukkit.block.BlockState;

public class GravelOreGenerationFeature extends AbstractOreUpperGeneratorFeature {

    private static final BlockState STATE = BlockGravel.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:overworld_underground_gravel_ore_feature";

    @Override
    public BlockState getState(BlockState original) {
        return STATE;
    }

    @Override
    public int getClusterCount() {
        return 14;
    }

    @Override
    public int getClusterSize() {
        return 33;
    }

    @Override
    public int getMinHeight() {
        return -64;
    }

    @Override
    public int getMaxHeight() {
        return 320;
    }

    @Override
    public String name() {
        return NAME;
    }
}
