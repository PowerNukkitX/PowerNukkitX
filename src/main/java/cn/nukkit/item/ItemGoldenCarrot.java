package cn.nukkit.item;

public class ItemGoldenCarrot extends ItemFood {
    /**
     * @deprecated 
     */
    
    public ItemGoldenCarrot() {
        super(GOLDEN_CARROT);
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
        return 14.4F;
    }
}