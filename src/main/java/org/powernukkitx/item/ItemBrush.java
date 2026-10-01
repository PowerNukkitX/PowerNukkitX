package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemBrush extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .canBeActivated(true)
            .maxDurability(65)
            .build();

    public ItemBrush() {
        super(BRUSH, DEFINITION);
    }
}