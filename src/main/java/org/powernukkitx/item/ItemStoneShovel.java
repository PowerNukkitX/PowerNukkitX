package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemStoneShovel extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(2)
            .maxDurability(ItemTool.DURABILITY_STONE)
            .shovel(true)
            .tier(ItemTool.TIER_STONE)
            .build();

    public ItemStoneShovel() {
        this(0, 1);
    }

    public ItemStoneShovel(Integer meta) {
        this(meta, 1);
    }

    public ItemStoneShovel(Integer meta, int count) {
        super(STONE_SHOVEL, meta, count, "Stone Shovel", DEFINITION);
    }
}
