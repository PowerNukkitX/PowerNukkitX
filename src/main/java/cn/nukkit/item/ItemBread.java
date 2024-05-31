package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBread extends ItemFood {
    /**
     * @deprecated 
     */
    

    public ItemBread() {
        this(1);
    }
    /**
     * @deprecated 
     */
    

    public ItemBread(int count) {
        super(BREAD, 0, count);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getFoodRestore() {
        return 5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getSaturationRestore() {
        return 6F;
    }
}
