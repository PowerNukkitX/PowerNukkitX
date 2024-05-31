package cn.nukkit.item;

public class ItemCookedSalmon extends ItemSalmon {
    /**
     * @deprecated 
     */
    
    public ItemCookedSalmon() {
        super(COOKED_SALMON, 0, 1);
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
        return 9.6F;
    }
}