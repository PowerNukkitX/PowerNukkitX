package cn.nukkit.level.generator.populator.nether;

import cn.nukkit.block.BlockBlackstone;
import cn.nukkit.block.BlockState;

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
