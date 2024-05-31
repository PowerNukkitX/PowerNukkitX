package cn.nukkit.item;

public class ItemBakedPotato extends ItemFood {
    /**
     * @deprecated 
     */
    
    public ItemBakedPotato() {
        super(BAKED_POTATO);
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
        return 7.2F;
    }
}