package cn.nukkit.level.generator.object.legacytree;

import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockWarpedStem;
import cn.nukkit.block.BlockWarpedWartBlock;

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