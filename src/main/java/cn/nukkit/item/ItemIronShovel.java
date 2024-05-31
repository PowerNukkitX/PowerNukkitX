package cn.nukkit.item;

public class ItemIronShovel extends ItemTool {
    /**
     * @deprecated 
     */
    
    public ItemIronShovel() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemIronShovel(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemIronShovel(Integer meta, int count) {
        super(IRON_SHOVEL, meta, count, "Iron Shovel");
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
    
    public boolean isShovel() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getTier() {
        return ItemTool.TIER_IRON;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getAttackDamage() {
        return 3;
    }
}