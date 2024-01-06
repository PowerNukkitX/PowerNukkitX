package cn.nukkit.network.protocol.types.itemstack.request.action;

import cn.nukkit.network.protocol.types.itemstack.request.ItemStackRequestSlotData;
import lombok.Value;

/**
 * ConsumeStackRequestAction is sent by the client when it uses an item to craft another item. The original
 * item is 'consumed'.
 */
@Value
public class ConsumeAction implements ItemStackRequestAction {
    int count;
    ItemStackRequestSlotData source;

    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.CONSUME;
    }
}
