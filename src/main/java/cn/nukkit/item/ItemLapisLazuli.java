package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemLapisLazuli extends ItemDye {
    /**
     * @deprecated 
     */
    
    public ItemLapisLazuli() {
        super(LAPIS_LAZULI);
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