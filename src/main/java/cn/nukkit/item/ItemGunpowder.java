package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemGunpowder extends Item {
    /**
     * @deprecated 
     */
    

    public ItemGunpowder() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemGunpowder(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemGunpowder(Integer meta, int count) {
        super(GUNPOWDER, meta, count, "Gunpowder");
    }
}
