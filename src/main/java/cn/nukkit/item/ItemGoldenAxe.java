package cn.nukkit.item;

public class ItemGoldenAxe extends ItemTool {
    /**
     * @deprecated 
     */
    
    public ItemGoldenAxe() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemGoldenAxe(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemGoldenAxe(Integer meta, int count) {
        super(GOLDEN_AXE, meta, count, "Golden Axe");
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
    
    public boolean isAxe() {
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
        return 3;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBreakShield() {
        return true;
    }
}