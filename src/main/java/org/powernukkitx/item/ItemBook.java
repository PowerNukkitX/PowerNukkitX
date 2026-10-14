package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBook extends Item {
    public static final ItemDefinition DEFINITION = DEFAULT_DEFINITION.toBuilder()
            .enchantAbility(1)
            .build();

    public ItemBook() {
        super(BOOK, DEFINITION);
    }
}
