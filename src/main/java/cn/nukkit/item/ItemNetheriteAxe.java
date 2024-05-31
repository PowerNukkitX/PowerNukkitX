package cn.nukkit.item;

public class ItemNetheriteAxe extends ItemTool {
    /**
     * @deprecated 
     */
    
    public ItemNetheriteAxe() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNetheriteAxe(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNetheriteAxe(Integer meta, int count) {
        super(NETHERITE_AXE, meta, count, "Netherite Axe");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return ItemTool.DURABILITY_NETHERITE;
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
        return ItemTool.TIER_NETHERITE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getAttackDamage() {
        return 8;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isLavaResistant() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBreakShield() {
        return true;
    }
}