package cn.nukkit.item;

public class ItemIronAxe extends ItemTool {
    /**
     * @deprecated 
     */
    
    public ItemIronAxe() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemIronAxe(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemIronAxe(Integer meta, int count) {
        super(IRON_AXE, meta, count, "Iron Axe");
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
    
    public boolean isAxe() {
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
        return 5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBreakShield() {
        return true;
    }
}