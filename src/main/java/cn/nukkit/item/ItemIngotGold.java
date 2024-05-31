package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemIngotGold extends Item {
    /**
     * @deprecated 
     */
    

    public ItemIngotGold() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemIngotGold(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemIngotGold(Integer meta, int count) {
        super(GOLD_INGOT, 0, count, "Gold Ingot");
    }
}
