package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemTurtleHelmet extends ItemArmor {
    public static final ItemDefinition DEFINITION = ARMOR.toBuilder()
            .armorPoints(2)
            .helmet(true)
            .maxDurability(276)
            .tier(Item.WEARABLE_TIER_OTHER)
            .toughness(2)
            .build();

    public ItemTurtleHelmet() {
        this(0, 1);
    }

    public ItemTurtleHelmet(Integer meta) {
        this(meta, 1);
    }

    public ItemTurtleHelmet(Integer meta, int count) {
        super(TURTLE_HELMET, meta, count, "Turtle Shell", DEFINITION);
    }

    @Override
    public int getEnchantAbility() {
        return 9;
    }
}
