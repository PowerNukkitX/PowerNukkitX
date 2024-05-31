package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemMagentaDye extends ItemDye {
    /**
     * @deprecated 
     */
    
    public ItemMagentaDye() {
        super(MAGENTA_DYE);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.MAGENTA;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {

    }
}