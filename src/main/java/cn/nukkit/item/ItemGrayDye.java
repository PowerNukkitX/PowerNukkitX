package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemGrayDye extends ItemDye {
    /**
     * @deprecated 
     */
    
    public ItemGrayDye() {
        super(GRAY_DYE);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GRAY;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {

    }
}