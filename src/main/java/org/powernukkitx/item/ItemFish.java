package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public abstract class ItemFish extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD;

    protected ItemFish(String id, Integer meta, int count) {
        this(id, meta, count, DEFINITION);
    }

    protected ItemFish(String id, Integer meta, int count, ItemDefinition definition) {
        super(id, meta, count, (String) null, definition);
    }
}
