package org.powernukkitx.item.armor.copper;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.definition.ItemDefinition;

public class ItemCopperHorseArmor extends Item {
    public static final ItemDefinition DEFINITION = Item.DEFAULT_DEFINITION.toBuilder()
            .maxStackSize(1)
            .build();

    public ItemCopperHorseArmor() {
        super(COPPER_HORSE_ARMOR, DEFINITION);
    }
}
