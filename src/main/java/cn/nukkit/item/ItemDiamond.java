package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemDiamond extends Item {
    /**
     * @deprecated 
     */
    

    public ItemDiamond() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemDiamond(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemDiamond(Integer meta, int count) {
        super(DIAMOND, 0, count, "Diamond");
    }
}
