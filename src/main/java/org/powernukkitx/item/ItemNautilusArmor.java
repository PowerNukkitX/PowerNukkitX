package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public abstract class ItemNautilusArmor extends Item {
    public static final ItemDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder()
            .maxStackSize(1)
            .build();

    public ItemNautilusArmor(String id, Integer meta, int count, String name) {
        this(id, meta, count, name, DEFINITION);
    }

    public ItemNautilusArmor(String id, Integer meta, int count, String name, ItemDefinition definition) {
        super(id, meta, count, name, definition);
    }
}
