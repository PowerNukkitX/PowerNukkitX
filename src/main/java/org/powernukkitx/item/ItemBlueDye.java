package org.powernukkitx.item;

import org.powernukkitx.utils.DyeColor;

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