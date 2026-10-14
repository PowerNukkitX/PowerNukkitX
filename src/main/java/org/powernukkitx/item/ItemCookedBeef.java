package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemCookedBeef extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .nutrition(8)
            .saturation(12.8f)
            .build();

    public ItemCookedBeef() {
        super(COOKED_BEEF, DEFINITION);
    }
}
