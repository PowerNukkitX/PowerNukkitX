package cn.nukkit.item;

/**
 * @author PetteriM1
 */
public class ItemDriedKelp extends ItemFood {
    /**
     * @deprecated 
     */
    

    public ItemDriedKelp() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemDriedKelp(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemDriedKelp(Integer meta, int count) {
        super(DRIED_KELP, 0, count, "Dried Kelp");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getFoodRestore() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getSaturationRestore() {
        return 0.6F;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getEatingTicks() {
        return 17;
    }
}
