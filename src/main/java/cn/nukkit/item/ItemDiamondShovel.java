package cn.nukkit.item;

public class ItemDiamondShovel extends ItemTool {
    /**
     * @deprecated 
     */
    
    public ItemDiamondShovel() {
        super(DIAMOND_SHOVEL);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return ItemTool.DURABILITY_DIAMOND;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isShovel() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getTier() {
        return ItemTool.TIER_DIAMOND;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getAttackDamage() {
        return 4;
    }
}