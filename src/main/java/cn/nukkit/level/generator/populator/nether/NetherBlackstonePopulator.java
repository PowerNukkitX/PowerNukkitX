package cn.nukkit.level.generator.populator.nether;

import cn.nukkit.block.BlockBlackstone;
import cn.nukkit.block.BlockGravel;
import cn.nukkit.block.BlockState;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.generator.feature.ore.OreGeneratorFeature;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.utils.random.RandomSourceProvider;

public class NetherBlackstonePopulator extends NetherGravelPopulator {

    public static final String NAME = "nether_blackstone";

    protected static final BlockState STATE = BlockBlackstone.PROPERTIES.getDefaultState();
    @Override
    public BlockState getState(BlockState original) {
        return STATE;
    }

    @Override
    public String name() {
        return NAME;
    }

}
