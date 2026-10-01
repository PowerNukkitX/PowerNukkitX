package org.powernukkitx.item;

import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author PetteriM1
 */
public class ItemDriedKelp extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .nutrition(1)
            .saturation(0.6f)
            .usingTicks(17)
            .build();

    public ItemDriedKelp() {
        this(0, 1);
    }

    public ItemDriedKelp(Integer meta) {
        this(meta, 1);
    }

    public ItemDriedKelp(Integer meta, int count) {
        super(DRIED_KELP, 0, count, "Dried Kelp", DEFINITION);
    }
}
