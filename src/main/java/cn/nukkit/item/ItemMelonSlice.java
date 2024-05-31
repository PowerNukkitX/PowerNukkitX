package cn.nukkit.item;

public class ItemMelonSlice extends ItemFood {
    /**
     * @deprecated 
     */
    
    public ItemMelonSlice() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemMelonSlice(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemMelonSlice(Integer meta, int count) {
        super(MELON_SLICE, meta, count, "Melon Slice");
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