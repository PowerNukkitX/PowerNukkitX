package org.powernukkitx.event.inventory;

import org.powernukkitx.entity.projectile.EntityThrownTrident;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.inventory.Inventory;


public class InventoryPickupTridentEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final EntityThrownTrident trident;

    public InventoryPickupTridentEvent(Inventory inventory, EntityThrownTrident trident) {
        super(inventory);
        this.trident = trident;
    }

    public EntityThrownTrident getTrident() {
        return trident;
    }
}
