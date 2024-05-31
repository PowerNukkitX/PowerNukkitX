package cn.nukkit.item;

public class ItemIronHelmet extends ItemArmor {
    /**
     * @deprecated 
     */
    
    public ItemIronHelmet() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemIronHelmet(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemIronHelmet(Integer meta, int count) {
        super(IRON_HELMET, meta, count, "Iron Helmet");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getTier() {
        return ItemArmor.TIER_IRON;
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
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return 166;
    }
}