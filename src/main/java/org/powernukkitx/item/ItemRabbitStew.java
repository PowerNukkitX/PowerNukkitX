package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author Snake1999
 * @since 2016/1/14
 */
public class ItemRabbitStew extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .maxStackSize(1)
            .nutrition(10)
            .saturation(12f)
            .build();

    public ItemRabbitStew() {
        this(0, 1);
    }

    public ItemRabbitStew(Integer meta) {
        this(meta, 1);
    }

    public ItemRabbitStew(Integer meta, int count) {
        super(RABBIT_STEW, meta, count, "Rabbit Stew", DEFINITION);
    }

    @Override
    public boolean onEaten(Player player) {
        player.getInventory().addItem(new ItemBowl());

        return super.onEaten(player);
    }
}
