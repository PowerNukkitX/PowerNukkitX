package cn.nukkit.item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemChestplateDiamond extends ItemArmor {
    /**
     * @deprecated 
     */
    

    public ItemChestplateDiamond() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemChestplateDiamond(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemChestplateDiamond(Integer meta, int count) {
        super(DIAMOND_CHESTPLATE, meta, count, "Diamond Chestplate");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getTier() {
        return ItemArmor.TIER_DIAMOND;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isChestplate() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getArmorPoints() {
        return 8;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return 529;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToughness() {
        return 2;
    }
}
