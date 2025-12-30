package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;

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
    private final ContainerSlotType slotType;
    private final int heldHotbarIndex;

    /**
     * @deprecated Use {@link #EntityInventoryChangeEvent(Entity, Item, Item, int, ContainerSlotType, int)}
     * so listeners know the slot type and currently selected hotbar index.
     */
    @Deprecated
    public EntityInventoryChangeEvent(Entity entity, Item oldItem, Item newItem, int slot) {
        this.entity = entity;
        this.oldItem = oldItem;
        this.newItem = newItem;
        this.slot = slot;
        this.slotType = ContainerSlotType.INVENTORY;
        this.heldHotbarIndex = -1;
    }

    public EntityInventoryChangeEvent(Entity entity, Item oldItem, Item newItem, int slot, ContainerSlotType slotType, int heldHotbarIndex) {
        this.entity = entity;
        this.oldItem = oldItem;
        this.newItem = newItem;
        this.slot = slot;
        this.slotType = slotType;
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

    public ContainerSlotType getSlotType() {
        return slotType;
    }

    public int getHeldHotbarIndex() {
        return heldHotbarIndex;
    }

    /**
     * @return true if this change targets the currently selected hotbar slot
     */
    public boolean isSelectedHotbar() {
        return this.slotType == ContainerSlotType.HOTBAR && this.slot == this.heldHotbarIndex && this.heldHotbarIndex >= 0;
    }
}
