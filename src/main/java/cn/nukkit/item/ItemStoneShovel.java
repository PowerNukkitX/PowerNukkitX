package cn.nukkit.item;

public class ItemStoneShovel extends ItemTool {
    /**
     * @deprecated 
     */
    

    public ItemStoneShovel() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemStoneShovel(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemStoneShovel(Integer meta, int count) {
        super(STONE_SHOVEL, meta, count, "Stone Shovel");
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
    
    public boolean isShovel() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getTier() {
        return ItemTool.TIER_STONE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getAttackDamage() {
        return 2;
    }
}