package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemMushroomStew extends ItemFood {
    public static final ItemDefinition DEFINITION = FOOD.toBuilder()
            .maxStackSize(1)
            .nutrition(6)
            .saturation(7.2f)
            .build();

    public ItemMushroomStew() {
        this(0, 1);
    }

    public ItemMushroomStew(Integer meta) {
        this(meta, 1);
    }

    public ItemMushroomStew(Integer meta, int count) {
        super(MUSHROOM_STEW, 0, count, "Mushroom Stew", DEFINITION);
    }

    @Override
    public boolean onEaten(Player player) {
        player.getInventory().addItem(new ItemBowl());

        return super.onEaten(player);
    }
}
