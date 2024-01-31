package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemBlackDye extends ItemDye {
    public ItemBlackDye() {
        super(BLACK_DYE);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLACK;
    }

    @Override
    public void setDamage(int meta) {
        
    }
}