package org.powernukkitx.event.inventory;

import org.powernukkitx.Player;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.inventory.Inventory;
/**
 * @author Box (Nukkit Project)
 */
public class InventoryCloseEvent extends InventoryEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Player who;

    public InventoryCloseEvent(Inventory inventory, Player who) {
        super(inventory);
        this.who = who;
    }

    public Player getPlayer() {
        return this.who;
    }
}
