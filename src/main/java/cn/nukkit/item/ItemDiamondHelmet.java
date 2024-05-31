package cn.nukkit.item;

public class ItemDiamondHelmet extends ItemArmor {
    /**
     * @deprecated 
     */
    
    public ItemDiamondHelmet() {
        super(DIAMOND_HELMET);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getTier() {
        return ItemArmor.TIER_DIAMOND;
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
        return 364;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToughness() {
        return 2;
    }
}