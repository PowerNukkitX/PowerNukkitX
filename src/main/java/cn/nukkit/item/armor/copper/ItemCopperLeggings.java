package cn.nukkit.item.armor.copper;

import cn.nukkit.item.ItemArmor;

public class ItemCopperLeggings extends ItemArmor {
    public ItemCopperLeggings() {
        super(COPPER_LEGGINGS);
    }

    @Override
    public boolean isLeggings() {
        return true;
    }

    @Override
    public int getTier() {
        return WEARABLE_TIER_COPPER;
    }

    @Override
    public int getArmorPoints() {
        return 3;
    }

    @Override
    public int getMaxDurability() {
        return 166;
    }

    @Override
    public int getToughness() {
        return 2;
    }
}