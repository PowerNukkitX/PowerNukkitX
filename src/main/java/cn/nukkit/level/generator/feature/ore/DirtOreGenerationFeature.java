package cn.nukkit.level.generator.feature.ore;

import cn.nukkit.block.BlockDirt;
import cn.nukkit.block.BlockState;

public class DirtOreGenerationFeature extends OreGeneratorFeature {

    private static final BlockState STATE = BlockDirt.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:overworld_underground_dirt_feature";

    @Override
    public BlockState getState(BlockState original) {
        return STATE;
    }

    @Override
    public int getClusterCount() {
        return 7;
    }

    @Override
    public int getClusterSize() {
        return 33;
    }

    @Override
    public int getMinHeight() {
        return 0;
    }

    @Override
    public int getMaxHeight() {
        return 160;
    }

    @Override
    public String name() {
        return NAME;
    }
}
