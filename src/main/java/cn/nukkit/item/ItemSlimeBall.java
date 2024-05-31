package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemSlimeBall extends Item {
    /**
     * @deprecated 
     */
    

    public ItemSlimeBall() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemSlimeBall(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemSlimeBall(Integer meta, int count) {
        super(SLIME_BALL, meta, count);
    }
}
