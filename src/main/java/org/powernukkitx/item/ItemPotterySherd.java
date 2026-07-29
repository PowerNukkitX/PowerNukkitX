package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;


public abstract class ItemPotterySherd extends Item {
    public static final ItemDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder().build();

    public ItemPotterySherd(String id) {
        this(id, DEFINITION);
    }

    public ItemPotterySherd(String id, ItemDefinition definition) {
        super(id, definition);
    }

    public ItemPotterySherd(String id, int count) {
        this(id, count, DEFINITION);
    }

    public ItemPotterySherd(String id, int count, ItemDefinition definition) {
        super(id, 0, count, definition);
    }
}
