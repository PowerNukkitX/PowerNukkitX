package cn.nukkit.item;

public class ItemStoneAxe extends ItemTool {
    /**
     * @deprecated 
     */
    

    public ItemStoneAxe() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemStoneAxe(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemStoneAxe(Integer meta, int count) {
        super(STONE_AXE, meta, count, "Stone Axe");
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
    
    public boolean isAxe() {
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
        return 4;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBreakShield() {
        return true;
    }
}