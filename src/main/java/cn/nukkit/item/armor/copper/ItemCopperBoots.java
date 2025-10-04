package cn.nukkit.item.armor.copper;

import cn.nukkit.item.ItemArmor;

public class ItemCopperBoots extends ItemArmor {
    public ItemCopperBoots() {
        super(COPPER_BOOTS);
    }

    @Override
    public int getTier() {
        return WEARABLE_TIER_COPPER;
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
        return 143;
    }

    @Override
    public int getToughness() {
        return 2;
    }
}