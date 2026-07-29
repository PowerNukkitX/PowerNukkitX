package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemTotemOfUndying extends Item {
    public static final ItemDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder()
            .maxStackSize(1)
            .build();

    public ItemTotemOfUndying() {
        super(TOTEM_OF_UNDYING, DEFINITION);
    }
}
