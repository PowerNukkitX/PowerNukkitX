package org.powernukkitx.item;

import org.powernukkitx.block.Block;
import org.powernukkitx.item.definition.ItemDefinition;


public abstract class ItemHangingSign extends Item {
    public static final ItemDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder()
            .maxStackSize(16)
            .build();

    public ItemHangingSign(String id) {
        this(id, DEFINITION);
    }

    public ItemHangingSign(String id, ItemDefinition definition) {
        super(id, definition);
        this.block = Block.get(id);
    }
}
