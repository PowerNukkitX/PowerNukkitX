package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemCyanDye extends ItemDye {
    public ItemCyanDye() {
        super(CYAN_DYE);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.CYAN;
    }
}