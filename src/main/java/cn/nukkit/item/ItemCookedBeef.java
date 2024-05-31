package cn.nukkit.item;

public class ItemCookedBeef extends ItemFood {
    /**
     * @deprecated 
     */
    
    public ItemCookedBeef() {
        super(COOKED_BEEF);
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