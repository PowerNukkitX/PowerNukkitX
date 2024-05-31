package cn.nukkit.item;

/**
 * ItemFish
 */
public class ItemCod extends ItemFish {
    /**
     * @deprecated 
     */
    
    public ItemCod() {
        super(COD, 0, 1);
    }

    
    /**
     * @deprecated 
     */
    protected ItemCod(String id, Integer meta, int count) {
        super(id, meta, count);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getFoodRestore() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getSaturationRestore() {
        return 0.4F;
    }
}