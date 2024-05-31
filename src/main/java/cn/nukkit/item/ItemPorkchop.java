package cn.nukkit.item;

public class ItemPorkchop extends ItemFood {
    /**
     * @deprecated 
     */
    
    public ItemPorkchop() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemPorkchop(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemPorkchop(Integer meta, int count) {
        super(PORKCHOP, meta, count, "Raw Porkchop");
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