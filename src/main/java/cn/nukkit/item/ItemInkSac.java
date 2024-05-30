package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemInkSac extends ItemDye {
    public ItemInkSac() {
        super(INK_SAC);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLACK;
    }

    @Override
    public void setDamage(int meta) {
    }
}