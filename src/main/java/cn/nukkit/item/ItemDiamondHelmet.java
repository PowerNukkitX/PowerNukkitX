package cn.nukkit.item;

public class ItemDiamondHelmet extends ItemArmor {
    public ItemDiamondHelmet() {
        super(DIAMOND_HELMET);
    }

    @Override
    public int getTier() {
        return Item.WEARABLE_TIER_DIAMOND;
    }

    @Override
    public boolean isHelmet() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 3;
    }

    @Override
    public int getMaxDurability() {
        return 364;
    }

    @Override
    public int getToughness() {
        return 2;
    }
}