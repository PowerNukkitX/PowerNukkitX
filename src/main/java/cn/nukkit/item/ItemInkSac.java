package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemInkSac extends ItemDye {
    /**
     * @deprecated 
     */
    
    public ItemInkSac() {
        super(INK_SAC);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLACK;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {
    }
}