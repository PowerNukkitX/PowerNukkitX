package cn.nukkit.item;

public class ItemCookedRabbit extends ItemFood {
    /**
     * @deprecated 
     */
    
    public ItemCookedRabbit() {
        super(COOKED_RABBIT);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getFoodRestore() {
        return 5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getSaturationRestore() {
        return 6F;
    }
}