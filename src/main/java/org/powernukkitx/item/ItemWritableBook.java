package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * alias BookAndQuill
 */
public class ItemWritableBook extends ItemBookWritable {
    public static final ItemDefinition DEFINITION = ItemBookWritable.DEFINITION.toBuilder()
            .maxStackSize(1)
            .build();

    public ItemWritableBook() {
        super(WRITABLE_BOOK, DEFINITION);
    }
}
