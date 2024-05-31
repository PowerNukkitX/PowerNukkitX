package cn.nukkit.item;

public class ItemNetheriteBoots extends ItemArmor {
    /**
     * @deprecated 
     */
    
    public ItemNetheriteBoots() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNetheriteBoots(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNetheriteBoots(Integer meta, int count) {
        super(NETHERITE_BOOTS, meta, count, "Netherite Boots");
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
    
    public boolean isBoots() {
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
        return 481;
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