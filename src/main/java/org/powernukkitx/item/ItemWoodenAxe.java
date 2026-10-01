package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemWoodenAxe extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(3)
            .axe(true)
            .canBreakShield(true)
            .maxDurability(ItemTool.DURABILITY_WOODEN)
            .tier(ItemTool.TIER_WOODEN)
            .build();

    public ItemWoodenAxe() {
        this(0, 1);
    }

    public ItemWoodenAxe(Integer meta) {
        this(meta, 1);
    }

    public ItemWoodenAxe(Integer meta, int count) {
        super(WOODEN_AXE, meta, count, "Wooden Axe", DEFINITION);
    }
}
