package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemPumpkinPie extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .nutrition(8)
            .saturation(4.8f)
            .build();

    public ItemPumpkinPie() {
        this(0, 1);
    }

    public ItemPumpkinPie(Integer meta) {
        this(meta, 1);
    }

    public ItemPumpkinPie(Integer meta, int count) {
        super(PUMPKIN_PIE, meta, count, "Pumpkin Pie", DEFINITION);
    }
}
