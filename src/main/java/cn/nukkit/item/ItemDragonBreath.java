package cn.nukkit.item;


public class ItemDragonBreath extends Item {
    /**
     * @deprecated 
     */
    


    public ItemDragonBreath() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemDragonBreath(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemDragonBreath(Integer meta, int count) {
        super(DRAGON_BREATH, meta, count, "Dragon's Breath");
    }
}
