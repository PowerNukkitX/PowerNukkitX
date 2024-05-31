package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemSugar extends Item {
    /**
     * @deprecated 
     */
    

    public ItemSugar() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemSugar(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemSugar(Integer meta, int count) {
        super(SUGAR, meta, count, "Sugar");
    }
}
