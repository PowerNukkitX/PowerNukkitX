package cn.nukkit.item;

public class ItemGoldenHoe extends ItemTool {
    /**
     * @deprecated 
     */
    
    public ItemGoldenHoe() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemGoldenHoe(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemGoldenHoe(Integer meta, int count) {
        super(GOLDEN_HOE, meta, count, "Golden Hoe");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return ItemTool.DURABILITY_GOLD;
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
        return ItemTool.TIER_GOLD;
    }
}