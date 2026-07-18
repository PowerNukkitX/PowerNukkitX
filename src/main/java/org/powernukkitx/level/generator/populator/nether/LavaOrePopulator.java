package org.powernukkitx.level.generator.populator.nether;

import org.powernukkitx.block.BlockLava;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.generator.feature.ore.OreGeneratorFeature;

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
