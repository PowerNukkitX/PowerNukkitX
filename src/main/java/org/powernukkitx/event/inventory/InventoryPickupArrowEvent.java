package org.powernukkitx.event.inventory;

import org.powernukkitx.entity.projectile.EntityArrow;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.inventory.Inventory;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class InventoryPickupArrowEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final EntityArrow arrow;

    public InventoryPickupArrowEvent(Inventory inventory, EntityArrow arrow) {
        super(inventory);
        this.arrow = arrow;
    }

    public EntityArrow getArrow() {
        return arrow;
    }
}
