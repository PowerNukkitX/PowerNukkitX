package cn.nukkit.item;

public class ItemStonePickaxe extends ItemTool {
    /**
     * @deprecated 
     */
    
    public ItemStonePickaxe() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemStonePickaxe(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemStonePickaxe(Integer meta, int count) {
        super(STONE_PICKAXE, meta, count, "Stone Pickaxe");
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
    
    public boolean isPickaxe() {
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
        return 3;
    }
}