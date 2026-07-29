package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemRabbit extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .nutrition(3)
            .saturation(1.8f)
            .build();

    public ItemRabbit() {
        this(0, 1);
    }

    public ItemRabbit(Integer meta) {
        this(meta, 1);
    }

    public ItemRabbit(Integer meta, int count) {
        super(RABBIT, meta, count, "Raw Rabbit", DEFINITION);
    }
}
