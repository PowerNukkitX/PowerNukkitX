package org.powernukkitx.item;

import org.powernukkitx.utils.DyeColor;

public class ItemMagentaDye extends ItemDye {
    public ItemMagentaDye() {
        super(MAGENTA_DYE);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.MAGENTA;
    }

    @Override
    public void setDamage(int meta) {

    }
}