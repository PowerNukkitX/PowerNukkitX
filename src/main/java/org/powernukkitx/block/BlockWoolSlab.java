package org.powernukkitx.block;

import org.powernukkitx.item.ItemTool;

import org.powernukkitx.tags.BlockTags;
import java.util.Set;

public abstract class BlockWoolSlab extends BlockSlab {
    protected BlockWoolSlab(BlockState blockState, String doubleSlab) {
        super(blockState, doubleSlab);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHEARS;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public boolean canHarvestWithHand() {
        return true;
    }
}

