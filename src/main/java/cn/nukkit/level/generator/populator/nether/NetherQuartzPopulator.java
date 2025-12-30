package cn.nukkit.level.generator.populator.nether;

import cn.nukkit.block.BlockQuartzOre;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.feature.ore.OreGeneratorFeature;

public class NetherQuartzPopulator extends OreGeneratorFeature {

    public static final String NAME = "nether_quartz";

    protected static final BlockState STATE = BlockQuartzOre.PROPERTIES.getDefaultState();
    @Override
    public BlockState getState(BlockState original) {
        return STATE;
    }

    @Override
    public int getClusterCount() {
        return 20;
    }

    @Override
    public int getClusterSize() {
        return 14;
    }

    @Override
    public int getMinHeight() {
        return 10;
    }

    @Override
    public int getMaxHeight() {
        return 117;
    }

    @Override
    public String name() {
        return NAME;
    }
}
