package cn.nukkit.level.generator.populator.nether;

import cn.nukkit.block.BlockLava;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.feature.ore.OreGeneratorFeature;

public class LavaOrePopulator extends OreGeneratorFeature {

    public static final String NAME = "nether_lava_ore";

    protected static final BlockState STATE = BlockLava.PROPERTIES.getDefaultState();
    @Override
    public BlockState getState(BlockState original) {
        return STATE;
    }

    @Override
    public int getClusterCount() {
        return 32;
    }

    @Override
    public int getClusterSize() {
        return 1;
    }

    @Override
    public int getMinHeight() {
        return 0;
    }

    @Override
    public int getMaxHeight() {
        return 32;
    }

    @Override
    public String name() {
        return NAME;
    }
}
