package cn.nukkit.item;

public class ItemGoldenBoots extends ItemArmor {
    /**
     * @deprecated 
     */
    
    public ItemGoldenBoots() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemGoldenBoots(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemGoldenBoots(Integer meta, int count) {
        super(GOLDEN_BOOTS, meta, count, "Golden Boots");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getTier() {
        return ItemArmor.TIER_GOLD;
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
        return 92;
    }
}