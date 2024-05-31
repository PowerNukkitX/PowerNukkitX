package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemPurpleDye extends ItemDye {
    /**
     * @deprecated 
     */
    
    public ItemPurpleDye() {
        super(PURPLE_DYE);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PURPLE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {

    }
}