package org.powernukkitx.item;

import org.powernukkitx.Player;
import org.powernukkitx.item.definition.ItemDefinition;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class ItemFood extends Item {
    public static final ItemDefinition FOOD = DEFAULT_DEFINITION.toBuilder()
            .edible(true)
            .usingTicks(31)
            .build();

    public ItemFood(String id) {
        this(id, 0, 1, (String) null, FOOD);
    }

    public ItemFood(String id, ItemDefinition definition) {
        this(id, 0, 1, (String) null, definition);
    }

    public ItemFood(String id, Integer meta) {
        this(id, meta, 1, (String) null, FOOD);
    }

    public ItemFood(String id, Integer meta, ItemDefinition definition) {
        this(id, meta, 1, (String) null, definition);
    }

    public ItemFood(String id, Integer meta, int count) {
        this(id, meta, count, (String) null, FOOD);
    }

    public ItemFood(String id, Integer meta, int count, ItemDefinition definition) {
        this(id, meta, count, (String) null, definition);
    }

    public ItemFood(String id, Integer meta, int count, String name) {
        this(id, meta, count, name, FOOD);
    }

    public ItemFood(String id, Integer meta, int count, String name, ItemDefinition definition) {
        super(id, meta, count, name, definition);
    }

    @Override
    public void whileUsing(Player player) {
        int ticksUsed = player.getLevel().getTick() - player.getLastUseTick(this.getId());
        if (ticksUsed >= getUsingTicks()) {
            if (this.onUse(player, ticksUsed)) {
                this.afterUse(player);
                player.clearLastUsedItem();
            }
        }
    }
}
