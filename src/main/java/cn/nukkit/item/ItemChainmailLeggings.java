package cn.nukkit.item;

public class ItemChainmailLeggings extends Item {
    /**
     * @deprecated 
     */
    
    public ItemChainmailLeggings() {
        super(CHAINMAIL_LEGGINGS);
    }
    /**
     * @deprecated 
     */
    

    public ItemChainmailLeggings(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemChainmailLeggings(Integer meta, int count) {
        super(CHAINMAIL_LEGGINGS, meta, count, "Chainmail Leggings");
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
    
    public boolean isLeggings() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getArmorPoints() {
        return 4;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxDurability() {
        return 226;
    }
}