package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;
import org.jetbrains.annotations.NotNull;

public abstract class ItemHarness extends Item {
    public static final ItemDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder()
            .maxStackSize(1)
            .build();

    public ItemHarness(@NotNull String id) {
        this(id, DEFINITION);
    }

    public ItemHarness(@NotNull String id, ItemDefinition definition) {
        super(id, definition);
    }
}
