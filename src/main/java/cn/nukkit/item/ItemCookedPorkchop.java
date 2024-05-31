package cn.nukkit.item;

public class ItemCookedPorkchop extends ItemFood {
    /**
     * @deprecated 
     */
    
    public ItemCookedPorkchop() {
        super(COOKED_PORKCHOP);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getFoodRestore() {
        return 8;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getSaturationRestore() {
        return 12.8F;
    }
}