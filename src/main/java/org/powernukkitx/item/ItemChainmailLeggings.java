package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemChainmailLeggings extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .armorPoints(4)
            .leggings(true)
            .maxDurability(226)
            .tier(Item.WEARABLE_TIER_CHAIN)
            .build();

    public ItemChainmailLeggings() {
        super(CHAINMAIL_LEGGINGS, DEFINITION);
    }

    public ItemChainmailLeggings(Integer meta) {
        this(meta, 1);
    }

    public ItemChainmailLeggings(Integer meta, int count) {
        super(CHAINMAIL_LEGGINGS, meta, count, "Chainmail Leggings", DEFINITION);
    }
}