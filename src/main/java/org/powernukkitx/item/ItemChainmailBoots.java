package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemChainmailBoots extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .armorPoints(1)
            .boots(true)
            .maxDurability(196)
            .tier(Item.WEARABLE_TIER_CHAIN)
            .build();

    public ItemChainmailBoots() {
        super(CHAINMAIL_BOOTS, DEFINITION);
    }

    public ItemChainmailBoots(Integer meta) {
        this(meta, 1);
    }

    public ItemChainmailBoots(Integer meta, int count) {
        super(CHAINMAIL_BOOTS, meta, count, "Chainmail Boots", DEFINITION);
    }
}