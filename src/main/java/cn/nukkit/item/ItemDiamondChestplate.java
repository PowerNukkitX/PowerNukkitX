package cn.nukkit.item;

public class ItemDiamondChestplate extends ItemArmor {
    public ItemDiamondChestplate() {
        super(DIAMOND_CHESTPLATE);
    }

    @Override
    public int getTier() {
        return ItemArmor.TIER_DIAMOND;
    }

    @Override
    public boolean isChestplate() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 8;
    }

    @Override
    public int getMaxDurability() {
        return 529;
    }

    @Override
    public int getToughness() {
        return 2;
    }
}