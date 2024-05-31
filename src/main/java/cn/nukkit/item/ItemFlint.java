package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemFlint extends Item {
    /**
     * @deprecated 
     */
    

    public ItemFlint() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemFlint(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemFlint(Integer meta, int count) {
        super(FLINT, meta, count, "Flint");
    }
}
