package cn.nukkit.item;

public class ItemNetheritePickaxe extends ItemTool {
    /**
     * @deprecated 
     */
    
    public ItemNetheritePickaxe() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNetheritePickaxe(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNetheritePickaxe(Integer meta, int count) {
        super(NETHERITE_PICKAXE, meta, count, "Netherite Pickaxe");
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
    
    public boolean isPickaxe() {
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
        return 6;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isLavaResistant() {
        return true;
    }
}