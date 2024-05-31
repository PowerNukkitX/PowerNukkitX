package cn.nukkit.item;

public class ItemIronLeggings extends ItemArmor {
    /**
     * @deprecated 
     */
    
    public ItemIronLeggings() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemIronLeggings(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemIronLeggings(Integer meta, int count) {
        super(IRON_LEGGINGS, meta, count, "Iron Leggings");
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
    
    public boolean isLeggings() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getArmorPoints() {
        return 5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return 226;
    }
}