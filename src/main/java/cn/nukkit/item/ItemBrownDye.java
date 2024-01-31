package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

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