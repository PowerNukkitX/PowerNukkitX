package cn.nukkit.event.inventory;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.Inventory;
/**
 * @author Box (Nukkit Project)
 */
public class InventoryCloseEvent extends InventoryEvent {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Player who;
    /**
     * @deprecated 
     */
    

    public InventoryCloseEvent(Inventory inventory, Player who) {
        super(inventory);
        this.who = who;
    }

    public Player getPlayer() {
        return this.who;
    }
}
