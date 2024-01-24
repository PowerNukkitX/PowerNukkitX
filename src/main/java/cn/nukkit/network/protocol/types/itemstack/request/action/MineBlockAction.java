package cn.nukkit.network.protocol.types.itemstack.request.action;

import lombok.Value;

/**
 * MineBlockStackRequestActionData is sent by the client when it breaks a block.
 */
@Value
public class MineBlockAction implements ItemStackRequestAction {
    int hotbarSlot;
    int predictedDurability;
    int stackNetworkId;

    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.MINE_BLOCK;
    }

}
