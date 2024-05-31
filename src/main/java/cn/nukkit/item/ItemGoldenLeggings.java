package cn.nukkit.item;

public class ItemGoldenLeggings extends ItemArmor {
    /**
     * @deprecated 
     */
    
    public ItemGoldenLeggings() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemGoldenLeggings(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemGoldenLeggings(Integer meta, int count) {
        super(GOLDEN_LEGGINGS, meta, count, "Golden Leggings");
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
    
    public boolean isLeggings() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getArmorPoints() {
        return 3;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return 106;
    }
}