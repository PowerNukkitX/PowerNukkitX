package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemShears extends ItemTool {
    /**
     * @deprecated 
     */
    

    public ItemShears() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemShears(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemShears(Integer meta, int count) {
        super(SHEARS, meta, count, "Shears");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return ItemTool.DURABILITY_SHEARS;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isShears() {
        return true;
    }
}
