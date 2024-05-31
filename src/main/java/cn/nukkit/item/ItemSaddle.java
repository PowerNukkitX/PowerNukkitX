package cn.nukkit.item;

public class ItemSaddle extends Item {
    /**
     * @deprecated 
     */
    
    public ItemSaddle() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemSaddle(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemSaddle(Integer meta, int count) {
        super(SADDLE, meta, count, "Saddle");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxStackSize() {
        return 1;
    }
}
