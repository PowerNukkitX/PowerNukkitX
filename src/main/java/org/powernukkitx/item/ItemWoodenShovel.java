package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemWoodenShovel extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(1)
            .maxDurability(ItemTool.DURABILITY_WOODEN)
            .shovel(true)
            .tier(ItemTool.TIER_WOODEN)
            .build();

    public ItemWoodenShovel() {
        this(0, 1);
    }

    public ItemWoodenShovel(Integer meta) {
        this(meta, 1);
    }

    public ItemWoodenShovel(Integer meta, int count) {
        super(WOODEN_SHOVEL, meta, count, "Wooden Shovel", DEFINITION);
    }
}
