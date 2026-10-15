package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemCookedSalmon extends ItemSalmon {
    public static final ItemDefinition DEFINITION = ItemSalmon.DEFINITION.toBuilder()
            .nutrition(6)
            .saturation(9.6f)
            .build();

    public ItemCookedSalmon() {
        super(COOKED_SALMON, 0, 1, DEFINITION);
    }
}
