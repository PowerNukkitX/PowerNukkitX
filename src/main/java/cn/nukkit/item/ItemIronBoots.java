package cn.nukkit.item;

public class ItemIronBoots extends ItemArmor {
    /**
     * @deprecated 
     */
    
    public ItemIronBoots() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemIronBoots(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemIronBoots(Integer meta, int count) {
        super(IRON_BOOTS, meta, count, "Iron Boots");
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
    
    public boolean isBoots() {
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
        return 196;
    }
}