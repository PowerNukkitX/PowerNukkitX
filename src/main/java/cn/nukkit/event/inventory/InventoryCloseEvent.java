package cn.nukkit.event.inventory;

import cn.nukkit.inventory.Inventory;
import cn.nukkit.player.Player;

/**
 * @author Box (Nukkit Project)
 */
public class InventoryCloseEvent extends InventoryEvent {

    private final Player who;

    public InventoryCloseEvent(Inventory inventory, Player who) {
        super(inventory);
        this.who = who;
    }

    public Player getPlayer() {
        return this.who;
    }
}
