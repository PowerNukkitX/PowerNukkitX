package cn.nukkit.item;

public class ItemDiamondChestplate extends ItemArmor {
    /**
     * @deprecated 
     */
    
    public ItemDiamondChestplate() {
        super(DIAMOND_CHESTPLATE);
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
        return 529;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToughness() {
        return 2;
    }
}