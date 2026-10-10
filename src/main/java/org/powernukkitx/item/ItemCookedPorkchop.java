package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemCookedPorkchop extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .nutrition(8)
            .saturation(12.8f)
            .build();

    public ItemCookedPorkchop() {
        super(COOKED_PORKCHOP, DEFINITION);
    }
}
