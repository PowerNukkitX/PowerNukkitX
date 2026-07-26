package org.powernukkitx.item;

import org.powernukkitx.utils.DyeColor;

public class ItemWhiteDye extends ItemDye {
    public ItemWhiteDye() {
        super(WHITE_DYE);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }

    @Override
    public void setDamage(int meta) {

    }
}