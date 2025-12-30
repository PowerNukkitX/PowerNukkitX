package cn.nukkit.item.armor.copper;

import cn.nukkit.item.ItemArmor;

public class ItemCopperHelmet extends ItemArmor {
    public ItemCopperHelmet() {
        super(COPPER_HELMET);
    }

    @Override
    public int getTier() {
        return WEARABLE_TIER_COPPER;
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
        return 122;
    }

    @Override
    public int getToughness() {
        return 2;
    }
}