package cn.nukkit.item;

public class ItemWoodenShovel extends ItemTool {
    /**
     * @deprecated 
     */
    

    public ItemWoodenShovel() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemWoodenShovel(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemWoodenShovel(Integer meta, int count) {
        super(WOODEN_SHOVEL, meta, count, "Wooden Shovel");
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
    
    public boolean isShovel() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getAttackDamage() {
        return 1;
    }
}