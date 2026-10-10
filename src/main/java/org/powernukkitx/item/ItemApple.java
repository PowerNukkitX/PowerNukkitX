package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemApple extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .nutrition(4)
            .saturation(2.4f)
            .build();

    public ItemApple() {
        super(APPLE, DEFINITION);
    }
}
