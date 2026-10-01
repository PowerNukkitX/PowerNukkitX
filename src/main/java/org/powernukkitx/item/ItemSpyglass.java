package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author LT_Name
 */

public class ItemSpyglass extends Item {
    public static final ItemDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder()
            .maxStackSize(1)
            .build();

    public ItemSpyglass() {
        this(0, 1);
    }

    public ItemSpyglass(Integer meta) {
        this(meta, 1);
    }

    public ItemSpyglass(Integer meta, int count) {
        super(SPYGLASS, meta, count, "Spyglass", DEFINITION);
    }
}
