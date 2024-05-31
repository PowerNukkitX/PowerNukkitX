package cn.nukkit.item;


public class ItemIngotNetherite extends Item {
    /**
     * @deprecated 
     */
    


    public ItemIngotNetherite() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemIngotNetherite(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemIngotNetherite(Integer meta, int count) {
        super(NETHERITE_INGOT, 0, count, "Netherite Ingot");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isLavaResistant() {
        return true;
    }
}
