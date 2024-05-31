package cn.nukkit.item;


public class ItemScrapNetherite extends Item {
    /**
     * @deprecated 
     */
    


    public ItemScrapNetherite() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemScrapNetherite(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemScrapNetherite(Integer meta, int count) {
        super(NETHERITE_SCRAP, 0, count, "Netherite Scrap");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isLavaResistant() {
        return true;
    }
}
