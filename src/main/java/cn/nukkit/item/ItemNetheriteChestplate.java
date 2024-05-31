package cn.nukkit.item;

public class ItemNetheriteChestplate extends ItemArmor {
    /**
     * @deprecated 
     */
    
    public ItemNetheriteChestplate() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNetheriteChestplate(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNetheriteChestplate(Integer meta, int count) {
        super(NETHERITE_CHESTPLATE, meta, count, "Netherite Chestplate");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getTier() {
        return ItemArmor.TIER_NETHERITE;
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
        return 8;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return 592;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToughness() {
        return 3;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isLavaResistant() {
        return true;
    }
}