package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemPinkDye extends ItemDye {
    /**
     * @deprecated 
     */
    
    public ItemPinkDye() {
        super(PINK_DYE);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PINK;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {

    }
}