package cn.nukkit.item;

/**
 * @author LT_Name
 */

public class ItemSpyglass extends Item {
    /**
     * @deprecated 
     */
    


    public ItemSpyglass() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemSpyglass(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemSpyglass(Integer meta, int count) {
        super(SPYGLASS, meta, count, "Spyglass");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxStackSize() {
        return 1;
    }
}
