package org.powernukkitx.level.generator.object.legacytree;

import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockWarpedStem;
import org.powernukkitx.block.BlockWarpedWartBlock;

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