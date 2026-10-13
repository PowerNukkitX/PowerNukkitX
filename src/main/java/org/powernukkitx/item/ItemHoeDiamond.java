package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemHoeDiamond extends ItemTool {
    public static final ItemDefinition DEFINITION = TOOL.toBuilder()
            .hoe(true)
            .maxDurability(ItemTool.DURABILITY_DIAMOND)
            .tier(ItemTool.TIER_DIAMOND)
            .build();

    public ItemHoeDiamond() {
        this(0, 1);
    }

    public ItemHoeDiamond(Integer meta) {
        this(meta, 1);
    }

    public ItemHoeDiamond(Integer meta, int count) {
        super(DIAMOND_HOE, meta, count, "Diamond Hoe", DEFINITION);
    }
}
