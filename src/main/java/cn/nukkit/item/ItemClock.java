package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemClock extends Item {
    /**
     * @deprecated 
     */
    

    public ItemClock() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemClock(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemClock(Integer meta, int count) {
        super(CLOCK, meta, count, "Clock");
    }
}
