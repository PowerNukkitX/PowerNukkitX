package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemBlueDye extends ItemDye {
    public ItemBlueDye() {
        super(BLUE_DYE);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLUE;
    }

    @Override
    public void setDamage(int meta) {
        
    }
}