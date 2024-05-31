package cn.nukkit.item;

public class ItemLeatherBoots extends ItemColorArmor {
    /**
     * @deprecated 
     */
    
    public ItemLeatherBoots() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemLeatherBoots(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemLeatherBoots(Integer meta, int count) {
        super(LEATHER_BOOTS, meta, count, "Leather Boots");
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
    
    public boolean isBoots() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getArmorPoints() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return 66;
    }
}