package cn.nukkit.item;

public class ItemDiamondSword extends ItemTool {
    /**
     * @deprecated 
     */
    
    public ItemDiamondSword() {
        super(DIAMOND_SWORD);
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
    
    public boolean isSword() {
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
        return 7;
    }
}