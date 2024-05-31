package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemLightGrayDye extends ItemDye {
    /**
     * @deprecated 
     */
    
    public ItemLightGrayDye() {
        super(LIGHT_GRAY_DYE);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_GRAY;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {

    }
}