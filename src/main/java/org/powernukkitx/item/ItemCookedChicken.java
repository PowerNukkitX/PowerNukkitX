package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemCookedChicken extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .nutrition(6)
            .saturation(7.2f)
            .build();

    public ItemCookedChicken() {
        super(COOKED_CHICKEN, DEFINITION);
    }
}
