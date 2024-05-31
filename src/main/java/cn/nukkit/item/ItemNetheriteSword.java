package cn.nukkit.item;

public class ItemNetheriteSword extends ItemTool {
    /**
     * @deprecated 
     */
    
    public ItemNetheriteSword() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNetheriteSword(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNetheriteSword(Integer meta, int count) {
        super(NETHERITE_SWORD, meta, count, "Netherite Sword");
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
    
    public boolean isSword() {
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
}