package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemGoldenCarrot extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .nutrition(6)
            .saturation(14.4f)
            .build();

    public ItemGoldenCarrot() {
        super(GOLDEN_CARROT, DEFINITION);
    }
}