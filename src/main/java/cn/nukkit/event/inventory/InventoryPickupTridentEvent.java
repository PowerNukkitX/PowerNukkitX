package cn.nukkit.event.inventory;

import cn.nukkit.entity.projectile.EntityThrownTrident;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.Inventory;


public class InventoryPickupTridentEvent extends InventoryEvent implements Cancellable {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final EntityThrownTrident trident;
    /**
     * @deprecated 
     */
    

    public InventoryPickupTridentEvent(Inventory inventory, EntityThrownTrident trident) {
        super(inventory);
        this.trident = trident;
    }

    public EntityThrownTrident getTrident() {
        return trident;
    }
}
