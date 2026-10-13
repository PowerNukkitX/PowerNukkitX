package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemNetheritePickaxe extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(6)
            .lavaResistant(true)
            .maxDurability(ItemTool.DURABILITY_NETHERITE)
            .pickaxe(true)
            .tier(ItemTool.TIER_NETHERITE)
            .build();

    public ItemNetheritePickaxe() {
        this(0, 1);
    }

    public ItemNetheritePickaxe(Integer meta) {
        this(meta, 1);
    }

    public ItemNetheritePickaxe(Integer meta, int count) {
        super(NETHERITE_PICKAXE, meta, count, "Netherite Pickaxe", DEFINITION);
    }
}
