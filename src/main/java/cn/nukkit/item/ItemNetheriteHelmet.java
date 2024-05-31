package cn.nukkit.item;

public class ItemNetheriteHelmet extends ItemArmor {
    /**
     * @deprecated 
     */
    
    public ItemNetheriteHelmet() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNetheriteHelmet(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNetheriteHelmet(Integer meta, int count) {
        super(NETHERITE_HELMET, meta, count, "Netherite Helmet");
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
    
    public boolean isHelmet() {
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
        return 407;
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