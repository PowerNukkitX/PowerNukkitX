package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemBlackDye extends ItemDye {
    /**
     * @deprecated 
     */
    
    public ItemBlackDye() {
        super(BLACK_DYE);
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