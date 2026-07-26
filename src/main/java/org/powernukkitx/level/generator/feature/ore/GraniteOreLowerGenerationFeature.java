package org.powernukkitx.level.generator.feature.ore;

import org.powernukkitx.block.BlockGranite;
import org.powernukkitx.block.BlockState;

public class GraniteOreLowerGenerationFeature extends AbstractOreUpperGeneratorFeature {

    private static final BlockState STATE = BlockGranite.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:overworld_underground_granite_lower_feature";

    @Override
    public BlockState getState(BlockState original) {
        return STATE;
    }

    @Override
    public int getClusterCount() {
        return 2;
    }

    @Override
    public int getClusterSize() {
        return 64;
    }

    @Override
    public int getMinHeight() {
        return 0;
    }

    @Override
    public int getMaxHeight() {
        return 60;
    }

    @Override
    public String name() {
        return NAME;
    }
}
