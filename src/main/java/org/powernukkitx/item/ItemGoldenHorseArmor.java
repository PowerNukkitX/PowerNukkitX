package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemGoldenHorseArmor extends Item {
    public static final ItemDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder()
            .maxStackSize(1)
            .build();

    public ItemGoldenHorseArmor() {
        super(GOLDEN_HORSE_ARMOR, DEFINITION);
    }
}