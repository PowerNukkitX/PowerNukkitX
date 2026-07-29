package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemIronShovel extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(3)
            .maxDurability(ItemTool.DURABILITY_IRON)
            .shovel(true)
            .tier(ItemTool.TIER_IRON)
            .build();

    public ItemIronShovel() {
        this(0, 1);
    }

    public ItemIronShovel(Integer meta) {
        this(meta, 1);
    }

    public ItemIronShovel(Integer meta, int count) {
        super(IRON_SHOVEL, meta, count, "Iron Shovel", DEFINITION);
    }
}