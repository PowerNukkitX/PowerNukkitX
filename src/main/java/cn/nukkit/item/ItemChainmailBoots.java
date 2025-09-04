package cn.nukkit.item;

public class ItemChainmailBoots extends ItemArmor {
    public ItemChainmailBoots() {
        super(CHAINMAIL_BOOTS);
    }

    public ItemChainmailBoots(Integer meta) {
        this(meta, 1);
    }

    public ItemChainmailBoots(Integer meta, int count) {
        super(CHAINMAIL_BOOTS, meta, count, "Chainmail Boots");
    }

    @Override
    public int getTier() {
        return Item.WEARABLE_TIER_CHAIN;
    }

    @Override
    public boolean isBoots() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 1;
    }

    @Override
    public int getMaxDurability() {
        return 196;
    }
}