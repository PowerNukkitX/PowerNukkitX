package cn.nukkit.level.generator.feature.ore;

import cn.nukkit.block.BlockAndesite;
import cn.nukkit.block.BlockState;

public class AndesiteOreLowerGenerationFeature extends GraniteOreLowerGenerationFeature {

    private static final BlockState STATE = BlockAndesite.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:overworld_underground_andesite_lower_feature";

    @Override
    public BlockState getState(BlockState original) {
        return STATE;
    }

    @Override
    public String name() {
        return NAME;
    }
}
