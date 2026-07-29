package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author CreeperFace
 */
public abstract class ItemMusicDisc extends Item {
    public static final ItemDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder()
            .maxStackSize(1)
            .build();

    protected ItemMusicDisc(String id) {
        this(id, DEFINITION);
    }

    protected ItemMusicDisc(String id, ItemDefinition definition) {
        super(id, definition);
    }

    public abstract String getSoundId();
}
