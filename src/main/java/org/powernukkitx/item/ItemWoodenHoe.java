package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemWoodenHoe extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .hoe(true)
            .maxDurability(ItemTool.DURABILITY_WOODEN)
            .tier(ItemTool.TIER_WOODEN)
            .build();

    public ItemWoodenHoe() {
        this(0, 1);
    }

    public ItemWoodenHoe(Integer meta) {
        this(meta, 1);
    }

    public ItemWoodenHoe(Integer meta, int count) {
        super(WOODEN_HOE, meta, count, "Wooden Hoe", DEFINITION);
    }
}
