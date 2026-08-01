package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemLeatherHorseArmor extends Item {
    public static final ItemDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder()
            .maxStackSize(1)
            .build();

    public ItemLeatherHorseArmor() {
        super(LEATHER_HORSE_ARMOR, DEFINITION);
    }
}
