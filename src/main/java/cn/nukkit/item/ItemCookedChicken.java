package cn.nukkit.item;

public class ItemCookedChicken extends ItemFood {
    /**
     * @deprecated 
     */
    
    public ItemCookedChicken() {
        super(COOKED_CHICKEN);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getFoodRestore() {
        return 6;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getSaturationRestore() {
        return 7.2F;
    }
}