package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * ItemFish
 */
public class ItemCod extends ItemFish {
    public static final ItemDefinition DEFINITION = ItemFish.DEFINITION.toBuilder()
            .nutrition(2)
            .saturation(0.4f)
            .build();

    public ItemCod() {
        this(COD, 0, 1, DEFINITION);
    }

    public ItemCod(ItemDefinition definition) {
        this(COD, 0, 1, definition);
    }

    protected ItemCod(String id, Integer meta, int count) {
        this(id, meta, count, DEFINITION);
    }

    protected ItemCod(String id, Integer meta, int count, ItemDefinition definition) {
        super(id, meta, count, definition);
    }
}
