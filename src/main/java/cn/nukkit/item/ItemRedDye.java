package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemRedDye extends ItemDye {
    public ItemRedDye() {
        super(RED_DYE);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.RED;
    }

    @Override
    public void setDamage(int meta) {
    }
}