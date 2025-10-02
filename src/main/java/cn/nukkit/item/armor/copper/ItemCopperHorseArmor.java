package cn.nukkit.item.armor.copper;

import cn.nukkit.item.Item;

public class ItemCopperHorseArmor extends Item {
    public ItemCopperHorseArmor() {
        super(COPPER_HORSE_ARMOR);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}