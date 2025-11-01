package cn.nukkit.level.generator.populator.nether;

import cn.nukkit.block.BlockNetherGoldOre;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.generator.feature.ore.OreGeneratorFeature;

public class NetherGoldOrePopulator extends OreGeneratorFeature {

    public static final String NAME = "nether_nether_gold_ore";

    protected static final BlockState STATE = BlockNetherGoldOre.PROPERTIES.getDefaultState();
    @Override
    public BlockState getState(BlockState original) {
        return STATE;
    }

    @Override
    public int getClusterCount() {
        return 10;
    }

    @Override
    public int getClusterSize() {
        return 10;
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
    public ConcentrationType getConcentration() {
        return ConcentrationType.TRIANGLE;
    }

    @Override
    public String name() {
        return NAME;
    }
}
