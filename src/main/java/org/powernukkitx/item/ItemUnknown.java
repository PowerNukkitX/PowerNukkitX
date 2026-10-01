package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemUnknown extends Item {
    public static final ItemDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder()
            .maxStackSize(64)
            .build();

    public ItemUnknown(String originalId, int meta, int count, byte[] tags) {
        super(originalId, meta, count, DEFINITION);
        if (tags != null && tags.length > 0) {
            this.setNbtBytes(tags);
        }
    }
}
