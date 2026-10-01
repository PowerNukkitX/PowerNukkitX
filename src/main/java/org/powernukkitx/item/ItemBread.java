package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBread extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .nutrition(5)
            .saturation(6f)
            .build();

    public ItemBread() {
        this(1);
    }

    public ItemBread(int count) {
        super(BREAD, 0, count, DEFINITION);
    }
}
