package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestActionType;
import cn.nukkit.network.protocol.types.itemstack.request.action.SwapAction;
import cn.nukkit.network.protocol.types.itemstack.response.ItemStackResponseContainer;
import cn.nukkit.network.protocol.types.itemstack.response.ItemStackResponseSlot;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Allay Project 2023/10/7
 *
 * @author daoge_cmd
 */
@Slf4j
public class SwapActionProcessor implements ItemStackRequestActionProcessor<SwapAction> {
    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.SWAP;
    }

    @Override
    public ActionResponse handle(SwapAction action, Player player, ItemStackRequestContext context) {
        ContainerSlotType $1 = action.getSource().getContainer();
        ContainerSlotType $2 = action.getDestination().getContainer();
        Inventory $3 = NetworkMapping.getInventory(player, sourceSlotType);
        Inventory $4 = NetworkMapping.getInventory(player, destinationSlotType);

        var $5 = source.fromNetworkSlot(action.getSource().getSlot());
        var $6 = destination.fromNetworkSlot(action.getDestination().getSlot());
        var $7 = source.getItem(sourceSlot);
        var $8 = destination.getItem(destinationSlot);
        if (validateStackNetworkId(sourceItem.getNetId(), action.getSource().getStackNetworkId())) {
            log.warn("mismatch stack network id!");
            return context.error();
        }
        if (validateStackNetworkId(destinationItem.getNetId(), action.getDestination().getStackNetworkId())) {
            log.warn("mismatch stack network id!");
            return context.error();
        }
        source.setItem(sourceSlot, destinationItem, false);
        destination.setItem(destinationSlot, sourceItem, false);
        return context.success(List.of(
                new ItemStackResponseContainer(
                        source.getSlotType(sourceSlot),
                        Lists.newArrayList(
                                new ItemStackResponseSlot(
                                        source.toNetworkSlot(sourceSlot),
                                        source.toNetworkSlot(sourceSlot),
                                        destinationItem.getCount(),
                                        destinationItem.getNetId(),
                                        destinationItem.getCustomName(),
                                        destinationItem.getDamage()
                                )
                        )
                ),
                new ItemStackResponseContainer(
                        destination.getSlotType(destinationSlot),
                        Lists.newArrayList(
                                new ItemStackResponseSlot(
                                        destination.toNetworkSlot(destinationSlot),
                                        destination.toNetworkSlot(destinationSlot),
                                        sourceItem.getCount(),
                                        sourceItem.getNetId(),
                                        sourceItem.getCustomName(),
                                        sourceItem.getDamage()
                                )
                        )
                )
        ));
    }
}
