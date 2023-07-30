package cn.nukkit.event.inventory;

import cn.nukkit.event.Cancellable;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.player.Player;

/**
 * @author boybook (Nukkit Project)
 */
public class InventoryClickEvent extends InventoryEvent implements Cancellable {

    private final int slot;
    private final Item sourceItem;
    private final Item heldItem;
    private final Player player;

    public InventoryClickEvent(Player who, Inventory inventory, int slot, Item sourceItem, Item heldItem) {
        super(inventory);
        this.slot = slot;
        this.sourceItem = sourceItem;
        this.heldItem = heldItem;
        this.player = who;
    }

    public int getSlot() {
        return slot;
    }

    public Item getSourceItem() {
        return sourceItem;
    }

    public Item getHeldItem() {
        return heldItem;
    }

    public Player getPlayer() {
        return player;
    }
}
