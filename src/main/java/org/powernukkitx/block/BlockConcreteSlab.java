package org.powernukkitx.block;

import org.powernukkitx.item.ItemTool;

public abstract class BlockConcreteSlab extends BlockSlab {
    protected BlockConcreteSlab(BlockState blockState, String doubleSlab) {
        super(blockState, doubleSlab);
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

