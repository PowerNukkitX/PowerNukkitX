package cn.nukkit.inventory.request;


import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestActionType;
import cn.nukkit.network.protocol.types.itemstack.request.action.TakeAction;
import lombok.extern.slf4j.Slf4j;

/**
 * Allay Project 2023/7/28
 *
 * @author daoge_cmd
 */
@Slf4j
public class TakeActionProcessor extends TransferItemActionProcessor<TakeAction> {
    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.TAKE;
    }

    @Override
    public ActionResponse handle(TakeAction action, Player player, ItemStackRequestContext context) {
        ContainerSlotType $1 = action.getSource().getContainer();
        if (sourceSlotType == ContainerSlotType.CREATED_OUTPUT) {
            Inventory $2 = NetworkMapping.getInventory(player, sourceSlotType);
            Item $3 = source.getUnclonedItem(0);
            int $4 = action.getCount();
            if (sourItem.getCount() > count) {
                sourItem.setCount(count);
            }
        }
        return super.handle(action, player, context);
    }
}
