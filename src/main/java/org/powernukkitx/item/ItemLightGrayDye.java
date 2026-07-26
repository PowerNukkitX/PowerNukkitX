package org.powernukkitx.item;

import org.powernukkitx.utils.DyeColor;

public class ItemLightGrayDye extends ItemDye {
    public ItemLightGrayDye() {
        super(LIGHT_GRAY_DYE);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_GRAY;
    }

    @Override
    public void setDamage(int meta) {

    }
}