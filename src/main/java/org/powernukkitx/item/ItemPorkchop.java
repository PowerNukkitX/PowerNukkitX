package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemPorkchop extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .nutrition(3)
            .saturation(1.8f)
            .build();

    public ItemPorkchop() {
        this(0, 1);
    }

    public ItemPorkchop(Integer meta) {
        this(meta, 1);
    }

    public ItemPorkchop(Integer meta, int count) {
        super(PORKCHOP, meta, count, "Raw Porkchop", DEFINITION);
    }
}
