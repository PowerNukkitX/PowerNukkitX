package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemCookie extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .nutrition(2)
            .saturation(0.4f)
            .build();

    public ItemCookie() {
        this(0, 1);
    }

    public ItemCookie(Integer meta) {
        this(meta, 1);
    }

    public ItemCookie(Integer meta, int count) {
        super(COOKIE, meta, count, "Cookie", DEFINITION);
    }
}
