package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemApple extends ItemFood {
    /**
     * @deprecated 
     */
    
    public ItemApple() {
        super(APPLE);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getFoodRestore() {
        return 4;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getSaturationRestore() {
        return 2.4F;
    }
}
