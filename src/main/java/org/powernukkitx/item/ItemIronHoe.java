package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemIronHoe extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .hoe(true)
            .maxDurability(ItemTool.DURABILITY_IRON)
            .tier(ItemTool.TIER_IRON)
            .build();

    public ItemIronHoe() {
        this(0, 1);
    }

    public ItemIronHoe(Integer meta) {
        this(meta, 1);
    }

    public ItemIronHoe(Integer meta, int count) {
        super(IRON_HOE, meta, count, "Iron Hoe", DEFINITION);
    }
}