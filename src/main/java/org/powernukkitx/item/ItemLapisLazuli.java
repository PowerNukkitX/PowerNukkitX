package org.powernukkitx.item;

import org.powernukkitx.utils.DyeColor;

public class ItemLapisLazuli extends ItemDye {
    public ItemLapisLazuli() {
        super(LAPIS_LAZULI);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLUE;
    }

    @Override
    public void setDamage(int meta) {
    }
}