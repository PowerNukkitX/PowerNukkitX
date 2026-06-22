package cn.nukkit.inventory.request;


import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.FullContainerName;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.TakeAction;

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
        FullContainerName containerName = action.getSource().getFullContainerName();
        ContainerEnumName sourceSlotType = containerName.getContainerName();
        if (sourceSlotType == ContainerEnumName.CREATED_OUTPUT_CONTAINER) {
            Integer dynamicId = containerName.getDynamicID();
            Inventory source = NetworkMapping.getInventory(player, sourceSlotType, dynamicId);
            Item sourItem = source.getUnclonedItem(0);
            int count = action.getCount();
            if (sourItem.getCount() > count) {
                Item capped = sourItem.clone();
                capped.setCount(count);
                source.setItem(0, capped);
            }
        }
        return super.handle(action, player, context);
    }
}
