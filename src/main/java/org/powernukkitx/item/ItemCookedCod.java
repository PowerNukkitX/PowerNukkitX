package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemCookedCod extends ItemCod {
    public static final ItemDefinition DEFINITION = ItemCod.DEFINITION.toBuilder()
            .nutrition(5)
            .saturation(6f)
            .build();

    public ItemCookedCod() {
        super(COOKED_COD, 0, 1, DEFINITION);
    }
}
