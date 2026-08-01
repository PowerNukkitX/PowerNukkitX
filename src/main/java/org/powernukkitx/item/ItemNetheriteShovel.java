package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemNetheriteShovel extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .attackDamage(5)
            .lavaResistant(true)
            .maxDurability(ItemTool.DURABILITY_NETHERITE)
            .shovel(true)
            .tier(ItemTool.TIER_NETHERITE)
            .build();

    public ItemNetheriteShovel() {
        this(0, 1);
    }

    public ItemNetheriteShovel(Integer meta) {
        this(meta, 1);
    }

    public ItemNetheriteShovel(Integer meta, int count) {
        super(NETHERITE_SHOVEL, meta, count, "Netherite Shovel", DEFINITION);
    }
}
