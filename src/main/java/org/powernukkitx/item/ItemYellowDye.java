package org.powernukkitx.item;

import org.powernukkitx.utils.DyeColor;

public class ItemYellowDye extends ItemDye {
    public ItemYellowDye() {
        super(YELLOW_DYE);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.YELLOW;
    }

    @Override
    public void setDamage(int meta) {
        
    }
}