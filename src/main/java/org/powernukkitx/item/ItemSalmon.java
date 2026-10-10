package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author Snake1999
 * @since 2016/1/14
 */
public class ItemSalmon extends ItemFish {
    public static final ItemDefinition DEFINITION = ItemFish.DEFINITION.toBuilder()
            .nutrition(2)
            .saturation(0.4f)
            .build();

    public ItemSalmon() {
        this(SALMON, 0, 1, DEFINITION);
    }

    public ItemSalmon(ItemDefinition definition) {
        this(SALMON, 0, 1, definition);
    }

    protected ItemSalmon(String id, Integer meta, int count) {
        this(id, meta, count, DEFINITION);
    }

    protected ItemSalmon(String id, Integer meta, int count, ItemDefinition definition) {
        super(id, meta, count, definition);
    }
}
