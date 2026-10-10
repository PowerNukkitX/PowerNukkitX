package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemIronAxe extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(5)
            .axe(true)
            .canBreakShield(true)
            .maxDurability(ItemTool.DURABILITY_IRON)
            .tier(ItemTool.TIER_IRON)
            .build();

    public ItemIronAxe() {
        this(0, 1);
    }

    public ItemIronAxe(Integer meta) {
        this(meta, 1);
    }

    public ItemIronAxe(Integer meta, int count) {
        super(IRON_AXE, meta, count, "Iron Axe", DEFINITION);
    }
}
