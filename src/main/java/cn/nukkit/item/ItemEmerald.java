package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemEmerald extends Item {
    /**
     * @deprecated 
     */
    

    public ItemEmerald() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemEmerald(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemEmerald(Integer meta, int count) {
        super(EMERALD, meta, count, "Emerald");
    }
}
