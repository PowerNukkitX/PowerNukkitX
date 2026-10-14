package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemShears extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .maxDurability(ItemTool.DURABILITY_SHEARS)
            .shears(true)
            .build();

    public ItemShears() {
        this(0, 1);
    }

    public ItemShears(Integer meta) {
        this(meta, 1);
    }

    public ItemShears(Integer meta, int count) {
        super(SHEARS, meta, count, "Shears", DEFINITION);
    }
}
