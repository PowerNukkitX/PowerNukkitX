package org.powernukkitx.level.generator.feature.ore;

import org.powernukkitx.block.BlockDiorite;
import org.powernukkitx.block.BlockState;

public class DioriteOreUpperGenerationFeature extends GraniteOreUpperGenerationFeature {

    private static final BlockState STATE = BlockDiorite.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:overworld_underground_diorite_upper_feature";

    @Override
    public BlockState getState(BlockState original) {
        return STATE;
    }

    @Override
    public String name() {
        return NAME;
    }
}
