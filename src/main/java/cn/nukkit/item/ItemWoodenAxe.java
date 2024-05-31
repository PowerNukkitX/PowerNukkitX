package cn.nukkit.item;

public class ItemWoodenAxe extends ItemTool {
    /**
     * @deprecated 
     */
    
    public ItemWoodenAxe() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemWoodenAxe(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemWoodenAxe(Integer meta, int count) {
        super(WOODEN_AXE, meta, count, "Wooden Axe");
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
    
    public boolean isAxe() {
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