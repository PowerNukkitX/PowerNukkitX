package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemWolfArmor extends ItemColorArmor {
    public static final ItemDefinition DEFINITION = ItemColorArmor.DEFINITION.toBuilder()
            .maxDurability(64)
            .build();

     public ItemWolfArmor() {
         super(WOLF_ARMOR, DEFINITION);
     }
}
