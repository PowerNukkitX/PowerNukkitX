package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemBeef extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .nutrition(3)
            .saturation(1.8f)
            .build();

    public ItemBeef() {
        super(BEEF, DEFINITION);
    }
}
