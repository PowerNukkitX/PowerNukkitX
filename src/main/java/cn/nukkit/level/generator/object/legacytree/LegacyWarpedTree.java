package cn.nukkit.level.generator.object.legacytree;

import cn.nukkit.block.*;

public class LegacyWarpedTree extends LegacyNetherTree {
    @Override
    protected BlockState getTrunkBlockState() {
        return BlockWarpedStem.PROPERTIES.getDefaultState();
    }

    @Override
    protected BlockState getLeafBlockState() {
        return BlockWarpedWartBlock.PROPERTIES.getDefaultState();
    }
}