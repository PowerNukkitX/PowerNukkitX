package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemGlowstoneDust extends Item {
    /**
     * @deprecated 
     */
    

    public ItemGlowstoneDust() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemGlowstoneDust(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemGlowstoneDust(Integer meta, int count) {
        super(GLOWSTONE_DUST, meta, count, "Glowstone Dust");
    }
}
