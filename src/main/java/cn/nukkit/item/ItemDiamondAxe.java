package cn.nukkit.item;

public class ItemDiamondAxe extends ItemTool {
    /**
     * @deprecated 
     */
    
    public ItemDiamondAxe() {
        super(DIAMOND_AXE);
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
    
    public boolean isAxe() {
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
        return 6;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBreakShield() {
        return true;
    }
}