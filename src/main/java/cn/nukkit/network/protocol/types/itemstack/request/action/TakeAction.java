package cn.nukkit.network.protocol.types.itemstack.request.action;

import cn.nukkit.network.protocol.types.itemstack.request.ItemStackRequestSlotData;
import lombok.Value;

/**
 * TakeStackRequestActionData is sent by the client to the server to take x amount of items from one slot in a
 * container to the cursor.
 */
@Value
public class TakeAction implements TransferItemStackRequestAction {
    int count;
    ItemStackRequestSlotData source;
    ItemStackRequestSlotData destination;

    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.TAKE;
    }
}
