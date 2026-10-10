package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemIronPickaxe extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(4)
            .maxDurability(ItemTool.DURABILITY_IRON)
            .pickaxe(true)
            .tier(ItemTool.TIER_IRON)
            .build();

    public ItemIronPickaxe() {
        this(0, 1);
    }

    public ItemIronPickaxe(Integer meta) {
        this(meta, 1);
    }

    public ItemIronPickaxe(Integer meta, int count) {
        super(IRON_PICKAXE, meta, count, "Iron Pickaxe", DEFINITION);
    }
}