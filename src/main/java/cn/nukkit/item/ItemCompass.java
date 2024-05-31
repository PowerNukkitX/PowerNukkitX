package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemCompass extends Item {
    /**
     * @deprecated 
     */
    

    public ItemCompass() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemCompass(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemCompass(Integer meta, int count) {
        super(COMPASS, meta, count, "Compass");
    }
}
