package cn.nukkit.item;

public class ItemChainmailHelmet extends ItemArmor {
    public ItemChainmailHelmet() {
        super(CHAINMAIL_HELMET);
    }

    public ItemChainmailHelmet(Integer meta) {
        this(meta, 1);
    }

    public ItemChainmailHelmet(Integer meta, int count) {
        super(CHAINMAIL_HELMET, meta, count, "Chainmail Helmet");
    }

    @Override
    public int getTier() {
        return Item.WEARABLE_TIER_CHAIN;
    }

    @Override
    public boolean isHelmet() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 2;
    }

    @Override
    public int getMaxDurability() {
        return 166;
    }
}