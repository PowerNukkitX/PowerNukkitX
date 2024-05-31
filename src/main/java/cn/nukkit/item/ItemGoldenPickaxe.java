package cn.nukkit.item;

public class ItemGoldenPickaxe extends ItemTool {
    /**
     * @deprecated 
     */
    
    public ItemGoldenPickaxe() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemGoldenPickaxe(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemGoldenPickaxe(Integer meta, int count) {
        super(GOLDEN_PICKAXE, meta, count, "Golden Pickaxe");
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
    
    public boolean isPickaxe() {
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
        return 2;
    }
}