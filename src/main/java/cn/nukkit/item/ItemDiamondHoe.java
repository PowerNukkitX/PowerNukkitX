package cn.nukkit.item;

public class ItemDiamondHoe extends ItemTool {
    /**
     * @deprecated 
     */
    
    public ItemDiamondHoe() {
        super(DIAMOND_HOE);
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
    
    public boolean isHoe() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getTier() {
        return ItemTool.TIER_DIAMOND;
    }
}