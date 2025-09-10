package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.inventory.CreativeOutputInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.SoleInventory;
import cn.nukkit.item.INBT;
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
        var srcFCN = action.getSource().getContainerName();
        var dstFCN = action.getDestination().getContainerName();

        ContainerSlotType sourceSlotType      = srcFCN.getContainer();
        ContainerSlotType destinationSlotType = dstFCN.getContainer();
        Integer dynamicSrc = srcFCN.getDynamicId();
        Integer dynamicDst = dstFCN.getDynamicId();

        Inventory source      = NetworkMapping.getInventory(player, sourceSlotType, dynamicSrc);
        Inventory destination = NetworkMapping.getInventory(player, destinationSlotType, dynamicDst);

        int sourceSlot = source.fromNetworkSlot(action.getSource().getSlot());
        int sourceStackNetworkId = action.getSource().getStackNetworkId();
        int destinationSlot = destination.fromNetworkSlot(action.getDestination().getSlot());
        int destinationStackNetworkId = action.getDestination().getStackNetworkId();
        int count = action.getCount();
        var sourItem = source.getUnclonedItem(sourceSlot);

        if (sourItem.isNull()) {
            log.debug("transfer an air item is not allowed");
            return context.error();
        }
        if(sourItem.isUsingNetId()) {
            if (validateStackNetworkId(sourItem.getNetId(), sourceStackNetworkId)) {
                log.warn("mismatch source stack network id!");
                return context.error();
            }
        }
        if (sourItem.getCount() < count) {
            log.warn("transfer an item that has not enough count is not allowed. Expected: {}, Actual: {}", sourItem.getCount(), count);
            return context.error();
        }

        if (context.has(CRAFT_CREATIVE_KEY) && (Boolean) context.get(CRAFT_CREATIVE_KEY)) {//If the player takes an item from creative mode, the destination is overridden directly
            if (source instanceof CreativeOutputInventory) {
                sourItem = sourItem.clone().autoAssignStackNetworkId();
                if(sourItem instanceof INBT inbt) {
                    inbt.onChange(destination);
                    Server.getInstance().getScheduler().scheduleTask(() -> destination.sendSlot(destinationSlot, player)); //sending the player the slot
                }
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
                        ),
                        action.getDestination().getContainerName()
                )));
            }
        }

        var destItem = destination.getUnclonedItem(destinationSlot);
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
        boolean sendSource = false; //Previous "!(source instanceof SoleInventory);", Not sending the source fixes the drag item distribution. Shouldn't cause any problems because inventory management is serversided.
        boolean sendDest = !(destination instanceof SoleInventory);

        if (sourItem.getCount() == count) { // first case：transfer all item
            Item newDest;
            if (!destItem.isNull()) {
                newDest = destItem.clone();
                newDest.setCount(destItem.getCount() + count);
            } else {
                if (source instanceof CreativeOutputInventory) {
                    sourItem = sourItem.clone().autoAssignStackNetworkId();
                }
                newDest = sourItem;
            }

            if (!destination.setItem(destinationSlot, newDest, sendDest)) {
                return context.error();
            }

            if (!source.clear(sourceSlot, sendSource)) {
                destination.setItem(destinationSlot, destItem, sendDest);
                return context.error();
            }

            resultDestItem = destination.getItem(destinationSlot);
            resultSourItem = source.getItem(sourceSlot);

        } else {// second case：transfer a part of item
            Item newDest;
            if (!destItem.isNull()) {
                newDest = destItem.clone();
                newDest.setCount(destItem.getCount() + count);
            } else {
                newDest = sourItem.clone().autoAssignStackNetworkId();
                newDest.setCount(count);
            }

            if (!destination.setItem(destinationSlot, newDest, sendDest)) {
                return context.error();
            }

            resultSourItem = sourItem.clone();
            resultSourItem.setCount(resultSourItem.getCount() - count);

            if (!source.setItem(sourceSlot, resultSourItem, sendSource)) {
                destination.setItem(destinationSlot, destItem, sendDest);
                return context.error();
            }

            resultDestItem = destination.getItem(destinationSlot);
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
                        ),
                        action.getDestination().getContainerName()
                );
        // CREATED_OUTPUT does not require a source response
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
                            ),
                            action.getSource().getContainerName()
                    ), destItemStackResponseSlot));
        }
    }
}
