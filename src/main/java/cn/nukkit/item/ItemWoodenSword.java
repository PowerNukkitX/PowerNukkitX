package cn.nukkit.item;

public class ItemWoodenSword extends ItemTool {
    /**
     * @deprecated 
     */
    

    public ItemWoodenSword() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemWoodenSword(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemWoodenSword(Integer meta, int count) {
        super(WOODEN_SWORD, meta, count, "Wooden Sword");
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
    
    public boolean isSword() {
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
        return 4;
    }
}