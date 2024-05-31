package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemCookie extends ItemFood {
    /**
     * @deprecated 
     */
    

    public ItemCookie() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemCookie(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemCookie(Integer meta, int count) {
        super(COOKIE, meta, count, "Cookie");
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
