package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemChainmailHelmet extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .armorPoints(2)
            .helmet(true)
            .maxDurability(166)
            .tier(Item.WEARABLE_TIER_CHAIN)
            .build();

    public ItemChainmailHelmet() {
        super(CHAINMAIL_HELMET, DEFINITION);
    }

    public ItemChainmailHelmet(Integer meta) {
        this(meta, 1);
    }

    public ItemChainmailHelmet(Integer meta, int count) {
        super(CHAINMAIL_HELMET, meta, count, "Chainmail Helmet", DEFINITION);
    }
}