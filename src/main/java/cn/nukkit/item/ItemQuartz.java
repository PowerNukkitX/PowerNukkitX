package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemQuartz extends Item {
    /**
     * @deprecated 
     */
    

    public ItemQuartz() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemQuartz(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemQuartz(Integer meta, int count) {
        super(QUARTZ, 0, count, "Nether Quartz");
    }
}
