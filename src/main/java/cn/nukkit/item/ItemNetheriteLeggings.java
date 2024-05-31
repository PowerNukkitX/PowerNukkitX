package cn.nukkit.item;

public class ItemNetheriteLeggings extends ItemArmor {
    /**
     * @deprecated 
     */
    

    public ItemNetheriteLeggings() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNetheriteLeggings(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNetheriteLeggings(Integer meta, int count) {
        super(NETHERITE_LEGGINGS, meta, count, "Netherite Leggings");
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
    
    public int getTier() {
        return ItemArmor.TIER_NETHERITE;
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
        return 555;
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