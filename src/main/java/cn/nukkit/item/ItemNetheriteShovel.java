package cn.nukkit.item;

public class ItemNetheriteShovel extends ItemTool {
    /**
     * @deprecated 
     */
    
    public ItemNetheriteShovel() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNetheriteShovel(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemNetheriteShovel(Integer meta, int count) {
        super(NETHERITE_SHOVEL, meta, count, "Netherite Shovel");
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
    
    public boolean isShovel() {
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
        return 5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isLavaResistant() {
        return true;
    }
}