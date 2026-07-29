package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemGoldenPickaxe extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(2)
            .maxDurability(ItemTool.DURABILITY_GOLD)
            .pickaxe(true)
            .tier(ItemTool.TIER_GOLD)
            .build();

    public ItemGoldenPickaxe() {
        this(0, 1);
    }

    public ItemGoldenPickaxe(Integer meta) {
        this(meta, 1);
    }

    public ItemGoldenPickaxe(Integer meta, int count) {
        super(GOLDEN_PICKAXE, meta, count, "Golden Pickaxe", DEFINITION);
    }
}