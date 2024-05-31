package cn.nukkit.item;

public class ItemDiamondBoots extends ItemArmor {
    /**
     * @deprecated 
     */
    
    public ItemDiamondBoots() {
        super(DIAMOND_BOOTS);
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
        return 430;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToughness() {
        return 2;
    }
}