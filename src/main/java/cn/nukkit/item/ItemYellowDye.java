package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemYellowDye extends ItemDye {
    /**
     * @deprecated 
     */
    
    public ItemYellowDye() {
        super(YELLOW_DYE);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.YELLOW;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {
        
    }
}