package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemNetheriteAxe extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(8)
            .axe(true)
            .canBreakShield(true)
            .lavaResistant(true)
            .maxDurability(ItemTool.DURABILITY_NETHERITE)
            .tier(ItemTool.TIER_NETHERITE)
            .build();

    public ItemNetheriteAxe() {
        this(0, 1);
    }

    public ItemNetheriteAxe(Integer meta) {
        this(meta, 1);
    }

    public ItemNetheriteAxe(Integer meta, int count) {
        super(NETHERITE_AXE, meta, count, "Netherite Axe", DEFINITION);
    }
}
