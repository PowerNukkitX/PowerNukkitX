package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemBlueDye extends ItemDye {
    /**
     * @deprecated 
     */
    
    public ItemBlueDye() {
        super(BLUE_DYE);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLUE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {
        
    }
}