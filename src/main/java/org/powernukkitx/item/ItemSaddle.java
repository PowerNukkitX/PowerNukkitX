package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemSaddle extends Item {
    public static final ItemDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder()
            .maxStackSize(1)
            .build();

    public ItemSaddle() {
        this(0, 1);
    }

    public ItemSaddle(Integer meta) {
        this(meta, 1);
    }

    public ItemSaddle(Integer meta, int count) {
        super(SADDLE, meta, count, "Saddle", DEFINITION);
    }
}
