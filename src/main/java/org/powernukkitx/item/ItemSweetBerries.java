package org.powernukkitx.item;

import org.powernukkitx.block.BlockSweetBerryBush;
import org.powernukkitx.item.definition.ItemDefinition;

public class ItemSweetBerries extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .nutrition(2)
            .saturation(0.4f)
            .build();

    public ItemSweetBerries() {
        this(0, 1);
    }

    public ItemSweetBerries(Integer meta) {
        this(meta, 1);
    }

    public ItemSweetBerries(Integer meta, int count) {
        super(SWEET_BERRIES, meta, count, "Sweet Berries", DEFINITION);
        this.block = new BlockSweetBerryBush();
    }
}
