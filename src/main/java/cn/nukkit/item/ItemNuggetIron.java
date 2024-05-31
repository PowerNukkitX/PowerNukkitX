package cn.nukkit.item;

/**
 * @author good777LUCKY
 */

public class ItemNuggetIron extends Item {
    /**
     * @deprecated 
     */
    


    public ItemNuggetIron() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNuggetIron(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNuggetIron(Integer meta, int count) {
        super(IRON_NUGGET, meta, count, "Iron Nugget");
    }
}
