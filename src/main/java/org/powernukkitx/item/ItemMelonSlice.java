package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

public class ItemMelonSlice extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .nutrition(2)
            .saturation(1.2f)
            .build();

    public ItemMelonSlice() {
        this(0, 1);
    }

    public ItemMelonSlice(Integer meta) {
        this(meta, 1);
    }

    public ItemMelonSlice(Integer meta, int count) {
        super(MELON_SLICE, meta, count, "Melon Slice", DEFINITION);
    }
}
