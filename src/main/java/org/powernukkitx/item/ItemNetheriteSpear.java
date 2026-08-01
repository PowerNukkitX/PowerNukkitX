package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author Buddelbubi
 * @since 2025/12/15
 */
public class ItemNetheriteSpear extends ItemSpear {
    public static final ItemDefinition DEFINITION = ItemSpear.DEFINITION.toBuilder()
            .attackDamage(6)
            .maxDurability(ItemTool.DURABILITY_NETHERITE)
            .tier(ItemTool.TIER_NETHERITE)
            .build();

    public ItemNetheriteSpear() {
        this(0, 1);
    }

    public ItemNetheriteSpear(Integer meta) {
        this(meta, 1);
    }

    public ItemNetheriteSpear(Integer meta, int count) {
        super(NETHERITE_SPEAR, meta, count, "Netherite Spear", DEFINITION);
    }
}
