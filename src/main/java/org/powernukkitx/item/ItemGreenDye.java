package org.powernukkitx.item;

import org.powernukkitx.utils.DyeColor;

public class ItemGreenDye extends ItemDye {
    public ItemGreenDye() {
        super(GREEN_DYE);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GREEN;
    }

    @Override
    public void setDamage(int meta) {

    }
}