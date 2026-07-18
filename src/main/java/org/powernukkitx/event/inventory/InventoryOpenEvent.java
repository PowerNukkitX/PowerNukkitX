package org.powernukkitx.event.inventory;

import org.powernukkitx.Player;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.inventory.Inventory;

/**
 * @author Box (Nukkit Project)
 */
public class InventoryOpenEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Player player;

    public InventoryOpenEvent(Inventory inventory, Player player) {
        super(inventory);
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }
}
