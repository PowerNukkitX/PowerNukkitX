package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemBeetrootSoup extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .maxStackSize(1)
            .nutrition(6)
            .saturation(7.2f)
            .build();

    public ItemBeetrootSoup() {
        this(0, 1);
    }

    public ItemBeetrootSoup(Integer meta) {
        this(meta, 1);
    }

    public ItemBeetrootSoup(Integer meta, int count) {
        super(BEETROOT_SOUP, 0, count, "Beetroot Soup", DEFINITION);
    }
}
