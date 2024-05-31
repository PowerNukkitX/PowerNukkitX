package cn.nukkit.item;

public class ItemGoldenSword extends ItemTool {
    /**
     * @deprecated 
     */
    
    public ItemGoldenSword() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemGoldenSword(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemGoldenSword(Integer meta, int count) {
        super(GOLDEN_SWORD, meta, count, "Golden Sword");
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
    
    public boolean isSword() {
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
        return 4;
    }
}