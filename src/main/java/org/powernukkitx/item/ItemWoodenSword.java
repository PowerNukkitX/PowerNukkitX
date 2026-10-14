package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemWoodenSword extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(4)
            .maxDurability(ItemTool.DURABILITY_WOODEN)
            .sword(true)
            .tier(ItemTool.TIER_WOODEN)
            .build();

    public ItemWoodenSword() {
        this(0, 1);
    }

    public ItemWoodenSword(Integer meta) {
        this(meta, 1);
    }

    public ItemWoodenSword(Integer meta, int count) {
        super(WOODEN_SWORD, meta, count, "Wooden Sword", DEFINITION);
    }
}
