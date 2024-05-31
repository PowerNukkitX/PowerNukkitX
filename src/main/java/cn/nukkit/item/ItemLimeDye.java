package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemLimeDye extends ItemDye {
    /**
     * @deprecated 
     */
    
    public ItemLimeDye() {
        super(LIME_DYE);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIME;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {
        
    }
}