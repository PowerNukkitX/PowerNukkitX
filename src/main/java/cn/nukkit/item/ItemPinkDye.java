package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemPinkDye extends ItemDye {
    public ItemPinkDye() {
        super(PINK_DYE);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PINK;
    }

    @Override
    public void setDamage(int meta) {

    }
}