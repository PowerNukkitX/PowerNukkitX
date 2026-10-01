package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemIngotNetherite extends Item {
    public static final ItemDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder()
            .lavaResistant(true)
            .build();

    public ItemIngotNetherite() {
        this(0, 1);
    }

    public ItemIngotNetherite(Integer meta) {
        this(meta, 1);
    }

    public ItemIngotNetherite(Integer meta, int count) {
        super(NETHERITE_INGOT, 0, count, "Netherite Ingot", DEFINITION);
    }
}
