package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemChainmailChestplate extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .armorPoints(5)
            .chestplate(true)
            .maxDurability(241)
            .tier(Item.WEARABLE_TIER_CHAIN)
            .build();

    public ItemChainmailChestplate() {
        super(CHAINMAIL_CHESTPLATE, DEFINITION);
    }

    public ItemChainmailChestplate(Integer meta) {
        this(meta, 1);
    }

    public ItemChainmailChestplate(Integer meta, int count) {
        super(CHAINMAIL_CHESTPLATE, meta, count, "Chainmail Chestplate", DEFINITION);
    }
}