package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemStick extends Item {
    /**
     * @deprecated 
     */
    

    public ItemStick() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemStick(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemStick(Integer meta, int count) {
        super(STICK, 0, count, "Stick");
    }
}
