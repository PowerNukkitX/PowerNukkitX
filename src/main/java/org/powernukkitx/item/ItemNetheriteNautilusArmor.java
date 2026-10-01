package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author Buddelbubi
 * @since 2025/12/16
 */
public class ItemNetheriteNautilusArmor extends ItemNautilusArmor {
    public static final ItemDefinition DEFINITION = ItemNautilusArmor.DEFINITION.toBuilder()
            .maxDurability(ItemTool.DURABILITY_NETHERITE)
            .tier(ItemTool.TIER_NETHERITE)
            .build();

    public ItemNetheriteNautilusArmor() {
        this(0, 1);
    }

    public ItemNetheriteNautilusArmor(Integer meta) {
        this(meta, 1);
    }

    public ItemNetheriteNautilusArmor(Integer meta, int count) {
        super(NETHERITE_NAUTILUS_ARMOR, meta, count, "Netherite Nautilus Armor", DEFINITION);
    }
}
