package cn.nukkit.inventory.request;


import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.PlaceAction;

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
