package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemStoneHoe extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .hoe(true)
            .maxDurability(ItemTool.DURABILITY_STONE)
            .tier(ItemTool.TIER_STONE)
            .build();

    public ItemStoneHoe() {
        this(0, 1);
    }

    public ItemStoneHoe(Integer meta) {
        this(meta, 1);
    }

    public ItemStoneHoe(Integer meta, int count) {
        super(STONE_HOE, meta, count, "Stone Hoe", DEFINITION);
    }
}
