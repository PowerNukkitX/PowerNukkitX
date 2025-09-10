package cn.nukkit.level.generator.feature.ore;

import cn.nukkit.block.BlockGranite;
import cn.nukkit.block.BlockState;

public class GraniteOreUpperGenerationFeature extends AbstractOreUpperGeneratorFeature {

    private static final BlockState STATE = BlockGranite.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:overworld_underground_granite_upper_feature";

    @Override
    public BlockState getState(BlockState original) {
        return STATE;
    }

    @Override
    public int getClusterCount() {
        return 6;
    }

    @Override
    public int getClusterSize() {
        return 64;
    }

    @Override
    public int getMinHeight() {
        return 64;
    }

    @Override
    public int getMaxHeight() {
        return 128;
    }

    @Override
    public boolean isRare() {
        return true;
    }

    @Override
    public String name() {
        return NAME;
    }
}
