package cn.nukkit.event.inventory;

import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.event.Cancellable;
import cn.nukkit.inventory.Inventory;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class InventoryPickupArrowEvent extends InventoryEvent implements Cancellable {

    private final EntityArrow arrow;

    public InventoryPickupArrowEvent(Inventory inventory, EntityArrow arrow) {
        super(inventory);
        this.arrow = arrow;
    }

    public EntityArrow getArrow() {
        return arrow;
    }
}
