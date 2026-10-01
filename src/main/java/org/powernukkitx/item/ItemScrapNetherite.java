package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemScrapNetherite extends Item {
    public static final ItemDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder()
            .lavaResistant(true)
            .build();

    public ItemScrapNetherite() {
        this(0, 1);
    }

    public ItemScrapNetherite(Integer meta) {
        this(meta, 1);
    }

    public ItemScrapNetherite(Integer meta, int count) {
        super(NETHERITE_SCRAP, 0, count, "Netherite Scrap", DEFINITION);
    }
}
