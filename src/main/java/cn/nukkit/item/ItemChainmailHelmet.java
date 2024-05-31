package cn.nukkit.item;

public class ItemChainmailHelmet extends ItemArmor {
    /**
     * @deprecated 
     */
    
    public ItemChainmailHelmet() {
        super(CHAINMAIL_HELMET);
    }
    /**
     * @deprecated 
     */
    

    public ItemChainmailHelmet(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemChainmailHelmet(Integer meta, int count) {
        super(CHAINMAIL_HELMET, meta, count, "Chainmail Helmet");
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
    
    public boolean isHelmet() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getArmorPoints() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return 166;
    }
}