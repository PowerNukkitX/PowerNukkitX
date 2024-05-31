package cn.nukkit.item;

public class ItemCookedMutton extends ItemFood {
    /**
     * @deprecated 
     */
    
    public ItemCookedMutton() {
        super(COOKED_MUTTON);
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