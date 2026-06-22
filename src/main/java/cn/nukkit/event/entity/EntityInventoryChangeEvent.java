package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EntityInventoryChangeEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Item oldItem;
    private Item newItem;
    private final int slot;
    private final ContainerEnumName containerEnumName;
    private final int heldHotbarIndex;

    /**
     * @deprecated Use {@link #EntityInventoryChangeEvent(Entity, Item, Item, int, ContainerEnumName, int)}
     * so listeners know the slot type and currently selected hotbar index.
     */
    @Deprecated
    public EntityInventoryChangeEvent(Entity entity, Item oldItem, Item newItem, int slot) {
        this.entity = entity;
        this.oldItem = oldItem;
        this.newItem = newItem;
        this.slot = slot;
        this.containerEnumName = ContainerEnumName.INVENTORY_CONTAINER;
        this.heldHotbarIndex = -1;
    }

    public EntityInventoryChangeEvent(Entity entity, Item oldItem, Item newItem, int slot, ContainerEnumName containerEnumName, int heldHotbarIndex) {
        this.entity = entity;
        this.oldItem = oldItem;
        this.newItem = newItem;
        this.slot = slot;
        this.containerEnumName = containerEnumName;
        this.heldHotbarIndex = heldHotbarIndex;
    }

    public int getSlot() {
        return slot;
    }

    public Item getNewItem() {
        return newItem;
    }

    public void setNewItem(Item newItem) {
        this.newItem = newItem;
    }

    public Item getOldItem() {
        return oldItem;
    }

    public ContainerEnumName getContainerEnumName() {
        return containerEnumName;
    }

    public int getHeldHotbarIndex() {
        return heldHotbarIndex;
    }

    /**
     * @return true if this change targets the currently selected hotbar slot
     */
    public boolean isSelectedHotbar() {
        return this.containerEnumName == ContainerEnumName.HOTBAR_CONTAINER && this.slot == this.heldHotbarIndex && this.heldHotbarIndex >= 0;
    }
}
