package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemMutton extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .nutrition(2)
            .saturation(1.2f)
            .build();

    public ItemMutton() {
        this(0, 1);
    }

    public ItemMutton(Integer meta) {
        this(meta, 1);
    }

    public ItemMutton(Integer meta, int count) {
        super(MUTTON, meta, count, "Raw Mutton", DEFINITION);
    }
}
