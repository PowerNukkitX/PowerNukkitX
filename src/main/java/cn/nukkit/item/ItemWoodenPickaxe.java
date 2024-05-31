package cn.nukkit.item;

public class ItemWoodenPickaxe extends ItemTool {
    /**
     * @deprecated 
     */
    
    public ItemWoodenPickaxe() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemWoodenPickaxe(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemWoodenPickaxe(Integer meta, int count) {
        super(WOODEN_PICKAXE, meta, count, "Wooden Pickaxe");
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
    
    public boolean isPickaxe() {
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
        return 2;
    }
}