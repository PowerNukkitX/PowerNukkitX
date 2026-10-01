package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemCookedRabbit extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .nutrition(5)
            .saturation(6f)
            .build();

    public ItemCookedRabbit() {
        super(COOKED_RABBIT, DEFINITION);
    }
}
