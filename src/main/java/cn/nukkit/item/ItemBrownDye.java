package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemBrownDye extends ItemDye {
    /**
     * @deprecated 
     */
    
    public ItemBrownDye() {
        super(BROWN_DYE);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BROWN;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {

    }
}