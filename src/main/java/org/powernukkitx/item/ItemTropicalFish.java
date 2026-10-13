package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemTropicalFish extends ItemFish {
    public static final ItemDefinition DEFINITION = ItemFish.DEFINITION.toBuilder()
            .nutrition(1)
            .saturation(0.2f)
            .build();

    public ItemTropicalFish() {
        super(TROPICAL_FISH, 0, 1, DEFINITION);
    }
}
