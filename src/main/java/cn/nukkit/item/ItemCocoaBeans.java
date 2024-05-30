package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.utils.DyeColor;

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