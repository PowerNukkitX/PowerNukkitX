package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemIronSword extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(6)
            .maxDurability(ItemTool.DURABILITY_IRON)
            .sword(true)
            .tier(ItemTool.TIER_IRON)
            .build();

    public ItemIronSword() {
        this(0, 1);
    }

    public ItemIronSword(Integer meta) {
        this(meta, 1);
    }

    public ItemIronSword(Integer meta, int count) {
        super(IRON_SWORD, meta, count, "Iron Sword", DEFINITION);
    }
}