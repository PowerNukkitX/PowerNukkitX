package cn.nukkit.item;

public class ItemLeatherChestplate extends ItemColorArmor {
    /**
     * @deprecated 
     */
    
    public ItemLeatherChestplate() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemLeatherChestplate(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemLeatherChestplate(Integer meta, int count) {
        super(LEATHER_CHESTPLATE, meta, count);
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
    
    public boolean isChestplate() {
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
        return 81;
    }
}