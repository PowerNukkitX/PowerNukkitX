package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemFeather extends Item {
    /**
     * @deprecated 
     */
    

    public ItemFeather() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemFeather(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemFeather(Integer meta, int count) {
        super(FEATHER, 0, count, "Feather");
    }
}
