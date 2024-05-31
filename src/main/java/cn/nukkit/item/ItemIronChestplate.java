package cn.nukkit.item;

public class ItemIronChestplate extends ItemArmor {
    /**
     * @deprecated 
     */
    

    public ItemIronChestplate() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemIronChestplate(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemIronChestplate(Integer meta, int count) {
        super(IRON_CHESTPLATE, meta, count, "Iron Chestplate");
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
    
    public boolean isChestplate() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getArmorPoints() {
        return 6;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return 241;
    }
}