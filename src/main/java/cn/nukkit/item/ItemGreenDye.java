package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemGreenDye extends ItemDye {
    /**
     * @deprecated 
     */
    
    public ItemGreenDye() {
        super(GREEN_DYE);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GREEN;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {

    }
}