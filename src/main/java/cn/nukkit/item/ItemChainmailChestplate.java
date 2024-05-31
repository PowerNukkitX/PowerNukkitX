package cn.nukkit.item;

public class ItemChainmailChestplate extends ItemArmor {
    /**
     * @deprecated 
     */
    
    public ItemChainmailChestplate() {
        super(CHAINMAIL_CHESTPLATE);
    }
    /**
     * @deprecated 
     */
    

    public ItemChainmailChestplate(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemChainmailChestplate(Integer meta, int count) {
        super(CHAINMAIL_CHESTPLATE, meta, count, "Chainmail Chestplate");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getTier() {
        return ItemArmor.TIER_CHAIN;
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
        return 5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return 241;
    }
}