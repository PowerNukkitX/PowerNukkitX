package cn.nukkit.level.generator.populator.nether;

import cn.nukkit.block.BlockMagma;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.feature.ore.OreGeneratorFeature;

public class MagmaPopulator extends OreGeneratorFeature {

    public static final String NAME = "nether_magma";

    protected static final BlockState STATE = BlockMagma.PROPERTIES.getDefaultState();
    @Override
    public BlockState getState(BlockState original) {
        return STATE;
    }

    @Override
    public int getClusterCount() {
        return 9;
    }

    @Override
    public int getClusterSize() {
        return 28;
    }

    @Override
    public int getMinHeight() {
        return 23;
    }

    @Override
    public int getMaxHeight() {
        return 36;
    }

    @Override
    public String name() {
        return NAME;
    }
}
