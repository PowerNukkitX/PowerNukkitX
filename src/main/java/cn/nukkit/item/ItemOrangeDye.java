package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemOrangeDye extends ItemDye {
    public ItemOrangeDye() {
        super(ORANGE_DYE);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.ORANGE;
    }

    @Override
    public void setDamage(int meta) {
        
    }
}