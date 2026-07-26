package org.powernukkitx.level.generator.object.legacytree;

import org.powernukkitx.block.BlockCrimsonStem;
import org.powernukkitx.block.BlockNetherWartBlock;
import org.powernukkitx.block.BlockState;

public class LegacyCrimsonTree extends LegacyNetherTree {
    @Override
    public BlockState getTrunkBlockState() {
        return BlockCrimsonStem.PROPERTIES.getDefaultState();
    }

    @Override
    public BlockState getLeafBlockState() {
        return BlockNetherWartBlock.PROPERTIES.getDefaultState();
    }
}
