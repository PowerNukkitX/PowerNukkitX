package cn.nukkit.item;

public class ItemWoodenHoe extends ItemTool {
    /**
     * @deprecated 
     */
    

    public ItemWoodenHoe() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemWoodenHoe(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemWoodenHoe(Integer meta, int count) {
        super(WOODEN_HOE, meta, count, "Wooden Hoe");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return ItemTool.DURABILITY_WOODEN;
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
        return ItemTool.TIER_WOODEN;
    }
}