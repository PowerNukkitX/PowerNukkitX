package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemWoodenPickaxe extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(2)
            .maxDurability(ItemTool.DURABILITY_WOODEN)
            .pickaxe(true)
            .tier(ItemTool.TIER_WOODEN)
            .build();

    public ItemWoodenPickaxe() {
        this(0, 1);
    }

    public ItemWoodenPickaxe(Integer meta) {
        this(meta, 1);
    }

    public ItemWoodenPickaxe(Integer meta, int count) {
        super(WOODEN_PICKAXE, meta, count, "Wooden Pickaxe", DEFINITION);
    }
}
