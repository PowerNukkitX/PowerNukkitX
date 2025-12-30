package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestActionType;

import javax.annotation.Nullable;

public interface ItemStackRequestActionProcessor<T extends ItemStackRequestAction> {
    ItemStackRequestActionType getType();

    @Nullable
    ActionResponse handle(T action, Player player, ItemStackRequestContext context);

    default boolean validateStackNetworkId(int expectedSNID, int clientSNID) {
        // If the stackNetworkId sent by the client is less than 0, it means the client guarantees the data is correct
        // and requests to follow the server's data.
        // This usually happens when an ItemStackRequest contains multiple actions that share the same source/destination container.
        // After the ID is checked in the first action, the subsequent actions don't need to check it again.
        return clientSNID > 0 && expectedSNID != clientSNID;
    }
}
