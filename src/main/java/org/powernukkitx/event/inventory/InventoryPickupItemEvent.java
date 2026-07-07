package org.powernukkitx.event.inventory;

import org.powernukkitx.entity.item.EntityItem;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.inventory.Inventory;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class InventoryPickupItemEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final EntityItem item;

    public InventoryPickupItemEvent(Inventory inventory, EntityItem item) {
        super(inventory);
        this.item = item;
    }

    public EntityItem getItem() {
        return item;
    }
}
