package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemNetherbrick extends Item {
    /**
     * @deprecated 
     */
    

    public ItemNetherbrick() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNetherbrick(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNetherbrick(Integer meta, int count) {
        super(NETHERBRICK, meta, count, "Nether Brick");
    }
}
