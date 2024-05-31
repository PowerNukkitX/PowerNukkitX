package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBeetrootSoup extends ItemFood {
    /**
     * @deprecated 
     */
    

    public ItemBeetrootSoup() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemBeetrootSoup(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemBeetrootSoup(Integer meta, int count) {
        super(BEETROOT_SOUP, 0, count, "Beetroot Soup");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getFoodRestore() {
        return 6;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getSaturationRestore() {
        return 7.2F;
    }
}
