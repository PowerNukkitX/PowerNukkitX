package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemNetheriteSword extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(8)
            .lavaResistant(true)
            .maxDurability(ItemTool.DURABILITY_NETHERITE)
            .sword(true)
            .tier(ItemTool.TIER_NETHERITE)
            .build();

    public ItemNetheriteSword() {
        this(0, 1);
    }

    public ItemNetheriteSword(Integer meta) {
        this(meta, 1);
    }

    public ItemNetheriteSword(Integer meta, int count) {
        super(NETHERITE_SWORD, meta, count, "Netherite Sword", DEFINITION);
    }
}
