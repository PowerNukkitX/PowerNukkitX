package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.TerracottaColor;

/**
 * @author xtypr
 * @since 2015/11/24
 */
public abstract class BlockTerracotta extends BlockSolid {
    public BlockTerracotta(BlockState blockState) {
        super(blockState);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 1.25;
    }

    @Override
    public double getResistance() {
        return 7;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }
}
