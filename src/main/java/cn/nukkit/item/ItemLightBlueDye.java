package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemLightBlueDye extends ItemDye {
    public ItemLightBlueDye() {
        super(LIGHT_BLUE_DYE);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_BLUE;
    }

    @Override
    public void setDamage(int meta) {
        
    }
}