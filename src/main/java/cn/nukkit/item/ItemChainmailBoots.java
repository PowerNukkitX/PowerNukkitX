package cn.nukkit.item;

public class ItemChainmailBoots extends ItemArmor {
    /**
     * @deprecated 
     */
    
    public ItemChainmailBoots() {
        super(CHAINMAIL_BOOTS);
    }
    /**
     * @deprecated 
     */
    

    public ItemChainmailBoots(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemChainmailBoots(Integer meta, int count) {
        super(CHAINMAIL_BOOTS, meta, count, "Chainmail Boots");
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
    
    public boolean isBoots() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getArmorPoints() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return 196;
    }
}