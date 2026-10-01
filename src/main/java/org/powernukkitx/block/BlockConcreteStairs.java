package org.powernukkitx.block;

import org.powernukkitx.item.ItemTool;

public abstract class BlockConcreteStairs extends BlockStairs {
    protected BlockConcreteStairs(BlockState blockState) {
        super(blockState);
    }

    @Override
    public double getResistance() {
        return 9;
    }

    @Override
    public double getHardness() {
        return 1.8;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}

