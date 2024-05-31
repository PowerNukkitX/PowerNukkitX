package cn.nukkit.item;

public class ItemStoneSword extends ItemTool {
    /**
     * @deprecated 
     */
    

    public ItemStoneSword() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemStoneSword(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemStoneSword(Integer meta, int count) {
        super(STONE_SWORD, meta, count, "Stone Sword");
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
    
    public boolean isSword() {
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
        return 5;
    }
}