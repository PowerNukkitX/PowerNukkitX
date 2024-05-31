package cn.nukkit.item;

public class ItemLeatherLeggings extends ItemColorArmor {
    /**
     * @deprecated 
     */
    
    public ItemLeatherLeggings() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemLeatherLeggings(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemLeatherLeggings(Integer meta, int count) {
        super(LEATHER_LEGGINGS, meta, count);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getTier() {
        return ItemArmor.TIER_LEATHER;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isLeggings() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getArmorPoints() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return 76;
    }
}