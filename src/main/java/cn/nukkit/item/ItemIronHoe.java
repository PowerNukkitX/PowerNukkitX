package cn.nukkit.item;

public class ItemIronHoe extends ItemTool {
    /**
     * @deprecated 
     */
    
    public ItemIronHoe() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemIronHoe(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemIronHoe(Integer meta, int count) {
        super(IRON_HOE, meta, count, "Iron Hoe");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return ItemTool.DURABILITY_IRON;
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
        return ItemTool.TIER_IRON;
    }
}