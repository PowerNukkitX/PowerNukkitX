package cn.nukkit.inventory.request;


import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestActionType;
import cn.nukkit.network.protocol.types.itemstack.request.action.PlaceAction;

/**
 * Allay Project 2023/7/26
 *
 * @author daoge_cmd
 */
public class PlaceActionProcessor extends TransferItemActionProcessor<PlaceAction> {
    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.PLACE;
    }
}
