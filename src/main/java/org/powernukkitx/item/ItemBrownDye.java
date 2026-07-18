package org.powernukkitx.item;

import org.powernukkitx.utils.DyeColor;

public class ItemBrownDye extends ItemDye {
    public ItemBrownDye() {
        super(BROWN_DYE);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BROWN;
    }

    @Override
    public void setDamage(int meta) {

    }
}