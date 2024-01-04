package cn.nukkit.event.inventory;

import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.Inventory;
import lombok.Getter;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class InventoryPickupItemEvent extends InventoryEvent implements Cancellable {

    @Getter
    private static final HandlerList handlers = new HandlerList();

    private final EntityItem item;

    public InventoryPickupItemEvent(Inventory inventory, EntityItem item) {
        super(inventory);
        this.item = item;
    }

    public EntityItem getItem() {
        return item;
    }
}
