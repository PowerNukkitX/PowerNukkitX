package cn.nukkit.item;

/**
 * @author Snake1999
 * @since 2016/1/14
 */
public class ItemSalmon extends ItemFish {
    /**
     * @deprecated 
     */
    
    public ItemSalmon() {
        super(SALMON, 0, 1);
    }

    
    /**
     * @deprecated 
     */
    protected ItemSalmon(String id, Integer meta, int count) {
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
