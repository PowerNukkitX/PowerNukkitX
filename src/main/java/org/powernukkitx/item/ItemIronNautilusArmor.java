package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author Buddelbubi
 * @since 2025/12/16
 */
public class ItemIronNautilusArmor extends ItemNautilusArmor {
    public static final ItemDefinition DEFINITION = ItemNautilusArmor.DEFINITION.toBuilder()
            .maxDurability(ItemTool.DURABILITY_IRON)
            .tier(ItemTool.TIER_IRON)
            .build();

    public ItemIronNautilusArmor() {
        this(0, 1);
    }

    public ItemIronNautilusArmor(Integer meta) {
        this(meta, 1);
    }

    public ItemIronNautilusArmor(Integer meta, int count) {
        super(IRON_NAUTILUS_ARMOR, meta, count, "Iron Nautilus Armor", DEFINITION);
    }

}