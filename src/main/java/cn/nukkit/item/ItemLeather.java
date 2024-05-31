package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemLeather extends Item {
    /**
     * @deprecated 
     */
    

    public ItemLeather() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemLeather(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemLeather(Integer meta, int count) {
        super(LEATHER, meta, count, "Leather");
    }
}
