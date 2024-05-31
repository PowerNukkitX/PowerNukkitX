package cn.nukkit.item;

public class ItemMutton extends ItemFood {
    /**
     * @deprecated 
     */
    
    public ItemMutton() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemMutton(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemMutton(Integer meta, int count) {
        super(MUTTON, meta, count, "Raw Mutton");
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
        return 1.2F;
    }
}