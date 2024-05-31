package cn.nukkit.item;

public class ItemStoneHoe extends ItemTool {
    /**
     * @deprecated 
     */
    
    public ItemStoneHoe() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemStoneHoe(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemStoneHoe(Integer meta, int count) {
        super(STONE_HOE, meta, count, "Stone Hoe");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return ItemTool.DURABILITY_STONE;
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
        return ItemTool.TIER_STONE;
    }
}