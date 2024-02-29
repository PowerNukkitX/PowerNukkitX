package cn.nukkit.level.generator.object.legacytree;

import cn.nukkit.block.BlockCrimsonStem;
import cn.nukkit.block.BlockNetherWartBlock;
import cn.nukkit.block.BlockState;

public class LegacyCrimsonTree extends LegacyNetherTree{
    @Override
    public BlockState getTrunkBlockState() {
        return BlockCrimsonStem.PROPERTIES.getDefaultState();
    }

    @Override
    public BlockState getLeafBlockState() {
        return BlockNetherWartBlock.PROPERTIES.getDefaultState();
    }
}
