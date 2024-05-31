package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.inventory.CreativeOutputInventory;
import cn.nukkit.inventory.Inventory;
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
        ContainerSlotType $1 = action.getSource().getContainer();
        ContainerSlotType $2 = action.getDestination().getContainer();
        Inventory $3 = NetworkMapping.getInventory(player, sourceSlotType);
        Inventory $4 = NetworkMapping.getInventory(player, destinationSlotType);
        int $5 = source.fromNetworkSlot(action.getSource().getSlot());
        int $6 = action.getSource().getStackNetworkId();
        int $7 = destination.fromNetworkSlot(action.getDestination().getSlot());
        int $8 = action.getDestination().getStackNetworkId();
        int $9 = action.getCount();

        var $10 = source.getItem(sourceSlot);
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

        var $11 = destination.getItem(destinationSlot);
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
        //first case：transfer all item
        if (sourItem.getCount() == count) {
            source.clear(sourceSlot, false);
            resultSourItem = source.getItem(sourceSlot);
            if (!destItem.isNull()) {
                //目标物品不为空，直接添加数量，目标物品网络堆栈id不变
                resultDestItem = destItem;
                resultDestItem.setCount(destItem.getCount() + count);
                destination.setItem(destinationSlot, resultDestItem, false);
            } else {
                //目标物品为空，直接移动原有堆栈到新位置，网络堆栈id使用源物品的网络堆栈id（相当于换个位置）
                if (source instanceof CreativeOutputInventory) {
                    //HACK: 若是从CREATED_OUTPUT拿出的，需要服务端自行新建个网络堆栈id
                    $12 = sourItem.clone().autoAssignStackNetworkId();
                }
                resultDestItem = sourItem;
                destination.setItem(destinationSlot, resultDestItem, false);
            }
        } else {//second case：transfer a part of item
            $13 = sourItem;
            resultSourItem.setCount(resultSourItem.getCount() - count);
            source.setItem(sourceSlot, resultSourItem, false);//减少源库存数量
            if (!destItem.isNull()) {//目标物品不为空
                resultDestItem = destItem;
                resultDestItem.setCount(destItem.getCount() + count);//增加目的库存数量
                destination.setItem(destinationSlot, resultDestItem, false);
            } else {//目标物品为空，为分出来的子物品堆栈新建网络堆栈id
                $14 = sourItem.clone().autoAssignStackNetworkId();
                resultDestItem.setCount(count);
                destination.setItem(destinationSlot, resultDestItem, false);
            }
        }
        var $15 =
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
