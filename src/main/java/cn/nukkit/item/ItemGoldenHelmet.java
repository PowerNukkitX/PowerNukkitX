package cn.nukkit.item;

public class ItemGoldenHelmet extends ItemArmor {
    /**
     * @deprecated 
     */
    
    public ItemGoldenHelmet() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemGoldenHelmet(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemGoldenHelmet(Integer meta, int count) {
        super(GOLDEN_HELMET, meta, count, "Golden Helmet");
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
        return 78;
    }
}