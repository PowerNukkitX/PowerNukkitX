package cn.nukkit.item;

public class ItemGoldenShovel extends ItemTool {
    /**
     * @deprecated 
     */
    
    public ItemGoldenShovel() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemGoldenShovel(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemGoldenShovel(Integer meta, int count) {
        super(GOLDEN_SHOVEL, meta, count, "Golden Shovel");
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
    
    public boolean isShovel() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getTier() {
        return ItemTool.TIER_GOLD;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getAttackDamage() {
        return 1;
    }
}