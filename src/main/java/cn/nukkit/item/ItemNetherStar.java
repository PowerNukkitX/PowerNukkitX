package cn.nukkit.item;

public class ItemNetherStar extends Item {
    /**
     * @deprecated 
     */
    

    public ItemNetherStar() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNetherStar(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNetherStar(Integer meta, int count) {
        super(NETHER_STAR, 0, count, "Nether Star");
    }
}
