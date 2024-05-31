package cn.nukkit.item;

public class ItemNetheriteHoe extends ItemTool {
    /**
     * @deprecated 
     */
    

    public ItemNetheriteHoe() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNetheriteHoe(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNetheriteHoe(Integer meta, int count) {
        super(NETHERITE_HOE, meta, count, "Netherite Hoe");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isHoe() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getAttackDamage() {
        return 6;
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
    
    public int getMaxDurability() {
        return ItemTool.DURABILITY_NETHERITE;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isLavaResistant() {
        return true;
    }
}