package cn.nukkit.network.protocol.types.itemstack.request.action;

import cn.nukkit.network.protocol.types.itemstack.request.ItemStackRequestSlotData;
import lombok.Value;

/**
 * DropStackRequestActionData is sent by the client when it drops an item out of the inventory when it has its
 * inventory opened. This action is not sent when a player drops an item out of the hotbar using the Q button
 * (or the equivalent on mobile). The InventoryTransaction packet is still used for that action, regardless of
 * whether the item stack network IDs are used or not.
 */
@Value
public class DropAction implements ItemStackRequestAction {
    int count;
    ItemStackRequestSlotData source;
    boolean randomly; // ?? Perhaps deals with order of items being dropped? Normally false.

    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.DROP;
    }
}
