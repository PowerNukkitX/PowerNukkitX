package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemDiamondChestplate extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .armorPoints(8)
            .chestplate(true)
            .maxDurability(529)
            .tier(Item.WEARABLE_TIER_DIAMOND)
            .toughness(2)
            .build();

    public ItemDiamondChestplate() {
        this(0, 1);
    }

    public ItemDiamondChestplate(Integer meta) {
        this(meta, 1);
    }

    public ItemDiamondChestplate(Integer meta, int count) {
        super(DIAMOND_CHESTPLATE, meta, count, "Diamond Chestplate", DEFINITION);
    }
}
