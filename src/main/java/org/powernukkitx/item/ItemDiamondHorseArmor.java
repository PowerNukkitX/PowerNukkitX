package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemDiamondHorseArmor extends Item {
    public static final ItemDefinition DEFINITION = Item.DEFAULT_DEFINITION.toBuilder()
            .maxStackSize(1)
            .build();

    public ItemDiamondHorseArmor() {
        super(DIAMOND_HORSE_ARMOR, DEFINITION);
    }
}
