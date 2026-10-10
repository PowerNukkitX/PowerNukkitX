package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemEnchantedBook extends Item {
    public static final ItemDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder()
            .applyEnchantments(false)
            .maxStackSize(1)
            .build();

    public ItemEnchantedBook() {
        this(ENCHANTED_BOOK, DEFINITION);
    }

    public ItemEnchantedBook(ItemDefinition definition) {
        this(ENCHANTED_BOOK, definition);
    }

    protected ItemEnchantedBook(String id) {
        this(id, DEFINITION);
    }

    protected ItemEnchantedBook(String id, ItemDefinition definition) {
        super(id, definition);
    }
}
