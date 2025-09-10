package cn.nukkit.item;

public class ItemDiamondLeggings extends ItemArmor {
    public ItemDiamondLeggings() {
        super(DIAMOND_LEGGINGS);
    }

    @Override
    public boolean isLeggings() {
        return true;
    }

    @Override
    public int getTier() {
        return Item.WEARABLE_TIER_DIAMOND;
    }

    @Override
    public int getArmorPoints() {
        return 6;
    }

    @Override
    public int getMaxDurability() {
        return 496;
    }

    @Override
    public int getToughness() {
        return 2;
    }
}