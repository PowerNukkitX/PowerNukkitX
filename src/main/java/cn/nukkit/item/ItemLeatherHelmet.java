package cn.nukkit.item;

public class ItemLeatherHelmet extends ItemColorArmor {
    /**
     * @deprecated 
     */
    
    public ItemLeatherHelmet() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemLeatherHelmet(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemLeatherHelmet(Integer meta, int count) {
        super(LEATHER_HELMET, meta, count);
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
    
    public boolean isHelmet() {
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
        return 56;
    }
}