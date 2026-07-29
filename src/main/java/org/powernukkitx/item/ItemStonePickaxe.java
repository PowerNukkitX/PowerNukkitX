package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemStonePickaxe extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(3)
            .maxDurability(ItemTool.DURABILITY_STONE)
            .pickaxe(true)
            .tier(ItemTool.TIER_STONE)
            .build();

    public ItemStonePickaxe() {
        this(0, 1);
    }

    public ItemStonePickaxe(Integer meta) {
        this(meta, 1);
    }

    public ItemStonePickaxe(Integer meta, int count) {
        super(STONE_PICKAXE, meta, count, "Stone Pickaxe", DEFINITION);
    }
}
