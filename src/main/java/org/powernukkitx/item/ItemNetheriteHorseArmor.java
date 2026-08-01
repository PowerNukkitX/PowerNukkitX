package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemNetheriteHorseArmor extends Item {
    public static final ItemDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder()
            .maxStackSize(1)
            .build();

    public ItemNetheriteHorseArmor() {
        super(NETHERITE_HORSE_ARMOR, DEFINITION);
    }
}
