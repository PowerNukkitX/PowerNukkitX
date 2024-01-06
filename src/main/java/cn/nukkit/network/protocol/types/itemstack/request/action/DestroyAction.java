package cn.nukkit.network.protocol.types.itemstack.request.action;

import cn.nukkit.network.protocol.types.itemstack.request.ItemStackRequestSlotData;
import lombok.Value;

/**
 * DestroyStackRequestActionData is sent by the client when it destroys an item in creative mode by moving it
 * back into the creative inventory.
 */
@Value
public class DestroyAction implements ItemStackRequestAction {
    int count;
    ItemStackRequestSlotData source;

    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.DESTROY;
    }
}
