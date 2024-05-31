package cn.nukkit.item;

public class ItemRabbit extends ItemFood {
    /**
     * @deprecated 
     */
    
    public ItemRabbit() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemRabbit(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemRabbit(Integer meta, int count) {
        super(RABBIT, meta, count, "Raw Rabbit");
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