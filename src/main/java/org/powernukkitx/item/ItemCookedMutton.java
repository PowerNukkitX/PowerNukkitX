package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemCookedMutton extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .nutrition(6)
            .saturation(9.6f)
            .build();

    public ItemCookedMutton() {
        super(COOKED_MUTTON, DEFINITION);
    }
}
