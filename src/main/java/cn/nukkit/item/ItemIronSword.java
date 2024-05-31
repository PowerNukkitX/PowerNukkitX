package cn.nukkit.item;

public class ItemIronSword extends ItemTool {
    /**
     * @deprecated 
     */
    
    public ItemIronSword() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemIronSword(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemIronSword(Integer meta, int count) {
        super(IRON_SWORD, meta, count, "Iron Sword");
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
    
    public boolean isSword() {
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
        return 6;
    }
}