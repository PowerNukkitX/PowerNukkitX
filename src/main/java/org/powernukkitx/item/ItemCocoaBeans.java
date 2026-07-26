package org.powernukkitx.item;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.utils.DyeColor;

public class ItemCocoaBeans extends ItemDye {
    public ItemCocoaBeans() {
        super(COCOA_BEANS);
        this.block = Block.get(BlockID.COCOA);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BROWN;
    }

    @Override
    public void setDamage(int meta) {
    }
}