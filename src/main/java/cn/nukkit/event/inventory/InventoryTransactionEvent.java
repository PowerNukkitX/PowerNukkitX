package cn.nukkit.event.inventory;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import lombok.Getter;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class InventoryTransactionEvent extends Event implements Cancellable {

    @Getter
    private static final HandlerList handlers = new HandlerList();

    private final InventoryTransaction transaction;

    public InventoryTransactionEvent(InventoryTransaction transaction) {
        this.transaction = transaction;
    }

    public InventoryTransaction getTransaction() {
        return transaction;
    }
}
