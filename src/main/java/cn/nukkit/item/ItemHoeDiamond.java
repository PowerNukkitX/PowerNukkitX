package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemHoeDiamond extends ItemTool {
    /**
     * @deprecated 
     */
    

    public ItemHoeDiamond() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemHoeDiamond(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemHoeDiamond(Integer meta, int count) {
        super(DIAMOND_HOE, meta, count, "Diamond Hoe");
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
