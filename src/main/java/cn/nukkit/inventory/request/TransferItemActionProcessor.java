package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.inventory.CreativeOutputInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import cn.nukkit.network.protocol.types.itemstack.request.action.TransferItemStackRequestAction;
import cn.nukkit.network.protocol.types.itemstack.response.ItemStackResponse;
import cn.nukkit.network.protocol.types.itemstack.response.ItemStackResponseContainer;
import cn.nukkit.network.protocol.types.itemstack.response.ItemStackResponseSlot;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public abstract class TransferItemActionProcessor<T extends TransferItemStackRequestAction> implements ItemStackRequestActionProcessor<T> {
    @Override
    public ItemStackResponse handle(T action, Player player, ItemStackRequestContext context) {
        ContainerSlotType sourceSlotType = action.getSource().getContainer();
        ContainerSlotType destinationSlotType = action.getDestination().getContainer();
        Inventory source = NetworkMapping.getInventory(player, sourceSlotType);
        Inventory destination = NetworkMapping.getInventory(player, destinationSlotType);
        int sourceSlot = source.fromNetworkSlot(action.getSource().getSlot());
        int sourceStackNetworkId = action.getSource().getStackNetworkId();
        int destinationSlot = destination.fromNetworkSlot(action.getDestination().getSlot());
        int destinationStackNetworkId = action.getDestination().getStackNetworkId();
        int count = action.getCount();
        var sourItem = source.getItem(sourceSlot);
        if (sourItem.isNull()) {
            log.warn("place an air item is not allowed");
            return context.error();
        }
        if (failToValidateStackNetworkId(sourItem.getNetId(), sourceStackNetworkId)) {
            log.warn("mismatch source stack network id!");
            return context.error();
        }
        if (sourItem.getCount() < count) {
            log.warn("place an item that has not enough count is not allowed");
            return context.error();
        }
        var destItem = destination.getItem(destinationSlot);
        if (!destItem.isNull() && destItem.equals(sourItem, true, true)) {
            log.warn("place an item to a slot that has a different item is not allowed");
            return context.error();
        }
        if (failToValidateStackNetworkId(destItem.getNetId(), destinationStackNetworkId)) {
            log.warn("mismatch destination stack network id!");
            return context.error();
        }
        if (destItem.getCount() + count > destItem.getMaxStackSize()) {
            log.warn("destination stack size bigger than the max stack size!");
            return context.error();
        }
        Item resultSourItem;
        Item resultDestItem;
        //第一种：全部拿走
        if (sourItem.getCount() == count) {
            resultSourItem = Item.AIR;
            source.setItem(sourceSlot, resultSourItem, false);
            if (!destItem.isNull()) {
                resultDestItem = destItem;
                //目标物品不为空，直接添加数量，目标物品网络堆栈id不变
                resultDestItem.setCount(destItem.getCount() + count);
                destination.setItem(sourceSlot, resultSourItem, false);
            } else {
                //目标物品为空，直接移动原有堆栈到新位置，网络堆栈id使用源物品的网络堆栈id（相当于换个位置）
                if (source instanceof CreativeOutputInventory) {
                    //HACK: 若是从CREATED_OUTPUT拿出的，需要服务端自行新建个网络堆栈id
                    sourItem = sourItem.clone().autoAssignStackNetworkId();
                }
                resultDestItem = sourItem;
                destination.setItem(destinationSlot, resultDestItem, false);
            }
        } else {//第二种：拿走一部分
            resultSourItem = sourItem;
            resultSourItem.setCount(resultSourItem.getCount() - count);
            source.setItem(sourceSlot, resultSourItem, false);
            if (!destItem.isNull()) {//目标物品不为空
                resultDestItem = destItem;
                resultDestItem.setCount(destItem.getCount() + count);
                source.setItem(sourceSlot, resultSourItem, false);
            } else {//目标物品为空，为分出来的子物品堆栈新建网络堆栈id
                resultDestItem = sourItem.clone().autoAssignStackNetworkId();
                resultDestItem.setCount(count);
                destination.setItem(destinationSlot, resultDestItem, false);
            }
        }
        var destItemStackResponseSlot =
                new ItemStackResponseContainer(
                        destination.getSlotType(destinationSlot),
                        List.of(
                                new ItemStackResponseSlot(
                                        destination.toNetworkSlot(destinationSlot),
                                        destination.toNetworkSlot(destinationSlot),
                                        resultDestItem.getCount(),
                                        resultDestItem.getNetId(),
                                        resultDestItem.getCustomName(),
                                        resultDestItem.getDamage()
                                )
                        )
                );
        //CREATED_OUTPUT不需要发响应
        if (source instanceof CreativeOutputInventory) {
            return context.success(List.of(destItemStackResponseSlot));
        } else {
            return context.success(List.of(
                    new ItemStackResponseContainer(
                            source.getSlotType(sourceSlot),
                            List.of(
                                    new ItemStackResponseSlot(
                                            source.toNetworkSlot(sourceSlot),
                                            source.toNetworkSlot(sourceSlot),
                                            resultSourItem.getCount(),
                                            resultSourItem.getNetId(),
                                            resultSourItem.getCustomName(),
                                            resultSourItem.getDamage()
                                    )
                            )
                    ), destItemStackResponseSlot));
        }
    }

}
