package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemBakedPotato extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .nutrition(5)
            .saturation(7.2f)
            .build();

    public ItemBakedPotato() {
        super(BAKED_POTATO, DEFINITION);
    }
}
