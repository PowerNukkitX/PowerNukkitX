package cn.nukkit.item;

public class ItemBeef extends ItemFood {
    /**
     * @deprecated 
     */
    
    public ItemBeef() {
        super(BEEF);
    }
    @Override
    /**
     * @deprecated 
     */
    
    public int getFoodRestore() {
        return 3;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getSaturationRestore() {
        return 1.8F;
    }
}