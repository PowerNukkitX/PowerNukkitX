package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemGoldenSword extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(4)
            .maxDurability(ItemTool.DURABILITY_GOLD)
            .sword(true)
            .tier(ItemTool.TIER_GOLD)
            .build();

    public ItemGoldenSword() {
        this(0, 1);
    }

    public ItemGoldenSword(Integer meta) {
        this(meta, 1);
    }

    public ItemGoldenSword(Integer meta, int count) {
        super(GOLDEN_SWORD, meta, count, "Golden Sword", DEFINITION);
    }
}
