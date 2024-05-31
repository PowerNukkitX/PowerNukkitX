package cn.nukkit.item;

import cn.nukkit.utils.DyeColor;

public class ItemBoneMeal extends ItemDye {
    /**
     * @deprecated 
     */
    
    public ItemBoneMeal() {
        super(BONE_MEAL);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isFertilizer() {
        return true;
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int meta) {
    }
}