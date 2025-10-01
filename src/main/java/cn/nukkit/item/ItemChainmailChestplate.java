package cn.nukkit.item;

public class ItemChainmailChestplate extends ItemArmor {
    public ItemChainmailChestplate() {
        super(CHAINMAIL_CHESTPLATE);
    }

    public ItemChainmailChestplate(Integer meta) {
        this(meta, 1);
    }

    public ItemChainmailChestplate(Integer meta, int count) {
        super(CHAINMAIL_CHESTPLATE, meta, count, "Chainmail Chestplate");
    }

    @Override
    public int getTier() {
        return Item.WEARABLE_TIER_CHAIN;
    }

    @Override
    public boolean isChestplate() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 5;
    }

    @Override
    public int getMaxDurability() {
        return 241;
    }
}