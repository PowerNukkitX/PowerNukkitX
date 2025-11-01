package cn.nukkit.level.generator.feature.ore;

import cn.nukkit.block.BlockDiorite;
import cn.nukkit.block.BlockState;

public class DioriteOreLowerGenerationFeature extends GraniteOreLowerGenerationFeature {

    private static final BlockState STATE = BlockDiorite.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:overworld_underground_diorite_lower_feature";

    @Override
    public BlockState getState(BlockState original) {
        return STATE;
    }

    @Override
    public String name() {
        return NAME;
    }
}
