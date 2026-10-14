package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemGoldenAxe extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(3)
            .axe(true)
            .canBreakShield(true)
            .maxDurability(ItemTool.DURABILITY_GOLD)
            .tier(ItemTool.TIER_GOLD)
            .build();

    public ItemGoldenAxe() {
        this(0, 1);
    }

    public ItemGoldenAxe(Integer meta) {
        this(meta, 1);
    }

    public ItemGoldenAxe(Integer meta, int count) {
        super(GOLDEN_AXE, meta, count, "Golden Axe", DEFINITION);
    }
}