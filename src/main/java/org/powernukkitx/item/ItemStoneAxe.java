package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemStoneAxe extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(4)
            .axe(true)
            .canBreakShield(true)
            .maxDurability(ItemTool.DURABILITY_STONE)
            .tier(ItemTool.TIER_STONE)
            .build();

    public ItemStoneAxe() {
        this(0, 1);
    }

    public ItemStoneAxe(Integer meta) {
        this(meta, 1);
    }

    public ItemStoneAxe(Integer meta, int count) {
        super(STONE_AXE, meta, count, "Stone Axe", DEFINITION);
    }
}
