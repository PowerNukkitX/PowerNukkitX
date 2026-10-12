package org.powernukkitx.block;

import org.powernukkitx.item.ItemTool;

public abstract class BlockConcreteDoubleSlab extends BlockDoubleSlabBase {
    protected BlockConcreteDoubleSlab(BlockState blockState) {
        super(blockState);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}

