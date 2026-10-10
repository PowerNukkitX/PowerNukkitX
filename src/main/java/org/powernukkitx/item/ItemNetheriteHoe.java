package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemNetheriteHoe extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(6)
            .hoe(true)
            .lavaResistant(true)
            .maxDurability(ItemTool.DURABILITY_NETHERITE)
            .tier(ItemTool.TIER_NETHERITE)
            .build();

    public ItemNetheriteHoe() {
        this(0, 1);
    }

    public ItemNetheriteHoe(Integer meta) {
        this(meta, 1);
    }

    public ItemNetheriteHoe(Integer meta, int count) {
        super(NETHERITE_HOE, meta, count, "Netherite Hoe", DEFINITION);
    }
}
