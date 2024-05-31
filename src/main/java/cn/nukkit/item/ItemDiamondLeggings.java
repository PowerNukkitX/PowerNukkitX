package cn.nukkit.item;

public class ItemDiamondLeggings extends ItemArmor {
    /**
     * @deprecated 
     */
    
    public ItemDiamondLeggings() {
        super(DIAMOND_LEGGINGS);
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
        return ItemArmor.TIER_DIAMOND;
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
        return 496;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToughness() {
        return 2;
    }
}