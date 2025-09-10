package cn.nukkit.item;

public class ItemDiamondBoots extends ItemArmor {
    public ItemDiamondBoots() {
        super(DIAMOND_BOOTS);
    }

    @Override
    public int getTier() {
        return Item.WEARABLE_TIER_DIAMOND;
    }

    @Override
    public boolean isBoots() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 3;
    }

    @Override
    public int getMaxDurability() {
        return 430;
    }

    @Override
    public int getToughness() {
        return 2;
    }
}