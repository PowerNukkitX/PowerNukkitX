package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemPumpkinPie extends ItemFood {
    /**
     * @deprecated 
     */
    

    public ItemPumpkinPie() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemPumpkinPie(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemPumpkinPie(Integer meta, int count) {
        super(PUMPKIN_PIE, meta, count, "Pumpkin Pie");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getFoodRestore() {
        return 8;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getSaturationRestore() {
        return 4.8F;
    }
}
