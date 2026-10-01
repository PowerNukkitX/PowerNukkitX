package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemGoldenHoe extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .hoe(true)
            .maxDurability(ItemTool.DURABILITY_GOLD)
            .tier(ItemTool.TIER_GOLD)
            .build();

    public ItemGoldenHoe() {
        this(0, 1);
    }

    public ItemGoldenHoe(Integer meta) {
        this(meta, 1);
    }

    public ItemGoldenHoe(Integer meta, int count) {
        super(GOLDEN_HOE, meta, count, "Golden Hoe", DEFINITION);
    }
}