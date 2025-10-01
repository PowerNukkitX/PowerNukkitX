package cn.nukkit.item.armor.copper;

import cn.nukkit.item.ItemArmor;

public class ItemCopperChestplate extends ItemArmor {
    public ItemCopperChestplate() {
        super(COPPER_CHESTPLATE);
    }

    @Override
    public int getTier() {
        return WEARABLE_TIER_COPPER;
    }

    @Override
    public boolean isChestplate() {
        return true;
    }

    @Override
    public int getArmorPoints() {
        return 4;
    }

    @Override
    public int getMaxDurability() {
        return 177;
    }

    @Override
    public int getToughness() {
        return 2;
    }
}