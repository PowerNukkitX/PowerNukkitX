package cn.nukkit.item;

public class ItemDiamondPickaxe extends ItemTool {
    /**
     * @deprecated 
     */
    
    public ItemDiamondPickaxe() {
        super(DIAMOND_PICKAXE);
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
    
    public boolean isPickaxe() {
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
        return 5;
    }
}