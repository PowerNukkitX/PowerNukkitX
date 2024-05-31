package cn.nukkit.item;

public class ItemGoldenChestplate extends ItemArmor {
    /**
     * @deprecated 
     */
    
    public ItemGoldenChestplate() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemGoldenChestplate(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemGoldenChestplate(Integer meta, int count) {
        super(GOLDEN_CHESTPLATE, meta, count, "Golden Chestplate");
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
    
    public boolean isChestplate() {
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
        return 113;
    }
}