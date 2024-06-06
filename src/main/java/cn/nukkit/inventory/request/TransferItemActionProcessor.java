package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.inventory.CreativeOutputInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.SoleInventory;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import cn.nukkit.network.protocol.types.itemstack.request.action.TransferItemStackRequestAction;
import cn.nukkit.network.protocol.types.itemstack.response.ItemStackResponseContainer;
import cn.nukkit.network.protocol.types.itemstack.response.ItemStackResponseSlot;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static cn.nukkit.inventory.request.CraftCreativeActionProcessor.CRAFT_CREATIVE_KEY;

@Slf4j
public abstract class TransferItemActionProcessor<T extends TransferItemStackRequestAction> implements ItemStackRequestActionProcessor<T> {
    @Override
    public ActionResponse handle(T action, Player player, ItemStackRequestContext context) {
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
            log.warn("transfer an air item is not allowed");
            return context.error();
        }
        if (validateStackNetworkId(sourItem.getNetId(), sourceStackNetworkId)) {
            log.warn("mismatch source stack network id!");
            return context.error();
        }
        if (sourItem.getCount() < count) {
            log.warn("transfer an item that has not enough count is not allowed");
            return context.error();
        }

        if (context.has(CRAFT_CREATIVE_KEY) && (Boolean) context.get(CRAFT_CREATIVE_KEY)) {//If the player takes an item from creative mode, the destination is overridden directly
            if (source instanceof CreativeOutputInventory) {
                sourItem = sourItem.clone().autoAssignStackNetworkId();
                destination.setItem(destinationSlot, sourItem, false);
                return context.success(List.of(new ItemStackResponseContainer(
                        destination.getSlotType(destinationSlot),
                        Lists.newArrayList(
                                new ItemStackResponseSlot(
                                        destination.toNetworkSlot(destinationSlot),
                                        destination.toNetworkSlot(destinationSlot),
                                        sourItem.getCount(),
                                        sourItem.getNetId(),
                                        sourItem.getCustomName(),
                                        sourItem.getDamage()
                                )
                        )
                )));
            }
        }

        var destItem = destination.getItem(destinationSlot);
        if (!destItem.isNull() && !destItem.equals(sourItem, true, true)) {
            log.warn("transfer an item to a slot that has a different item is not allowed");
            return context.error();
        }
        if (validateStackNetworkId(destItem.getNetId(), destinationStackNetworkId)) {
            log.warn("mismatch destination stack network id!");
            return context.error();
        }
        if (destItem.getCount() + count > destItem.getMaxStackSize()) {
            log.warn("destination stack size bigger than the max stack size!");
            return context.error();
        }

        Item resultSourItem;
        Item resultDestItem;
        boolean sendSource = !(source instanceof SoleInventory);
        boolean sendDest = !(destination instanceof SoleInventory);
        //first case：transfer all item
        if (sourItem.getCount() == count) {
            source.clear(sourceSlot, sendSource);
            resultSourItem = source.getItem(sourceSlot);
            if (!destItem.isNull()) {
                //目标物品不为空，直接添加数量，目标物品网络堆栈id不变
                resultDestItem = destItem;
                resultDestItem.setCount(destItem.getCount() + count);
                destination.setItem(destinationSlot, resultDestItem, sendDest);
            } else {
                //目标物品为空，直接移动原有堆栈到新位置，网络堆栈id使用源物品的网络堆栈id（相当于换个位置）
                if (source instanceof CreativeOutputInventory) {
                    //HACK: 若是从CREATED_OUTPUT拿出的，需要服务端自行新建个网络堆栈id
                    sourItem = sourItem.clone().autoAssignStackNetworkId();
                }
                resultDestItem = sourItem;
                destination.setItem(destinationSlot, resultDestItem, sendDest);
            }
        } else {//second case：transfer a part of item
            resultSourItem = sourItem;
            resultSourItem.setCount(resultSourItem.getCount() - count);
            source.setItem(sourceSlot, resultSourItem, sendSource);//减少源库存数量
            if (!destItem.isNull()) {//目标物品不为空
                resultDestItem = destItem;
                resultDestItem.setCount(destItem.getCount() + count);//增加目的库存数量
                destination.setItem(destinationSlot, resultDestItem, sendDest);
            } else {//目标物品为空，为分出来的子物品堆栈新建网络堆栈id
                resultDestItem = sourItem.clone().autoAssignStackNetworkId();
                resultDestItem.setCount(count);
                destination.setItem(destinationSlot, resultDestItem, sendDest);
            }
        }
        var destItemStackResponseSlot =
                new ItemStackResponseContainer(
                        destination.getSlotType(destinationSlot),
                        Lists.newArrayList(
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
        //CREATED_OUTPUT不需要发source响应
        if (source instanceof CreativeOutputInventory) {
            return context.success(List.of(destItemStackResponseSlot));
        } else {
            return context.success(List.of(
                    new ItemStackResponseContainer(
                            source.getSlotType(sourceSlot),
                            Lists.newArrayList(
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
