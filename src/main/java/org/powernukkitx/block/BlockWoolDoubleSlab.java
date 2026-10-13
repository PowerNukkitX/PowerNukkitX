package org.powernukkitx.block;

import org.powernukkitx.item.ItemTool;

public abstract class BlockWoolDoubleSlab extends BlockDoubleSlabBase {
    protected BlockWoolDoubleSlab(BlockState blockState) {
        super(blockState);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHEARS;
    }
}

