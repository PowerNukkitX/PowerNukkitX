package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemIngotIron extends Item {
    /**
     * @deprecated 
     */
    

    public ItemIngotIron() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemIngotIron(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemIngotIron(Integer meta, int count) {
        super(IRON_INGOT, 0, count, "Iron Ingot");
    }
}
