package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemStoneSword extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(5)
            .maxDurability(ItemTool.DURABILITY_STONE)
            .sword(true)
            .tier(ItemTool.TIER_STONE)
            .build();

    public ItemStoneSword() {
        this(0, 1);
    }

    public ItemStoneSword(Integer meta) {
        this(meta, 1);
    }

    public ItemStoneSword(Integer meta, int count) {
        super(STONE_SWORD, meta, count, "Stone Sword", DEFINITION);
    }
}
