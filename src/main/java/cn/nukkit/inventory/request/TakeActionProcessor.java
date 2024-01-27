package cn.nukkit.inventory.request;


import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestActionType;
import cn.nukkit.network.protocol.types.itemstack.request.action.TakeAction;

/**
 * Allay Project 2023/7/28
 *
 * @author daoge_cmd
 */
public class TakeActionProcessor extends TransferItemActionProcessor<TakeAction> {
    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.TAKE;
    }
}
