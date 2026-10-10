package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemIronHorseArmor extends Item {
    public static final ItemDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder()
            .maxStackSize(1)
            .build();

    public ItemIronHorseArmor() {
        super(IRON_HORSE_ARMOR, DEFINITION);
    }
}