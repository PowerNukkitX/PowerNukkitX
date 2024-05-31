package cn.nukkit.item;

public class ItemIronPickaxe extends ItemTool {
    /**
     * @deprecated 
     */
    
    public ItemIronPickaxe() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemIronPickaxe(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemIronPickaxe(Integer meta, int count) {
        super(IRON_PICKAXE, meta, count, "Iron Pickaxe");
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
    
    public boolean isPickaxe() {
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
        return 4;
    }
}