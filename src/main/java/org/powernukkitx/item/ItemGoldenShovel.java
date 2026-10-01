package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemGoldenShovel extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(1)
            .maxDurability(ItemTool.DURABILITY_GOLD)
            .shovel(true)
            .tier(ItemTool.TIER_GOLD)
            .build();

    public ItemGoldenShovel() {
        this(0, 1);
    }

    public ItemGoldenShovel(Integer meta) {
        this(meta, 1);
    }

    public ItemGoldenShovel(Integer meta, int count) {
        super(GOLDEN_SHOVEL, meta, count, "Golden Shovel", DEFINITION);
    }
}
