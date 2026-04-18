package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.FullContainerName;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.SwapAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseContainerInfo;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseSlotInfo;

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
        FullContainerName sourceContainerName = action.getSource().getFullContainerName();
        FullContainerName destinationContainerName = action.getDestination().getFullContainerName();
        Integer dynamicSrc = sourceContainerName.getDynamicID();
        Integer dynamicDst = destinationContainerName.getDynamicID();
        ContainerEnumName sourceSlotType = sourceContainerName.getContainerName();
        ContainerEnumName destinationSlotType = destinationContainerName.getContainerName();

        Inventory source = NetworkMapping.getInventory(player, sourceSlotType, dynamicSrc);
        Inventory destination = NetworkMapping.getInventory(player, destinationSlotType, dynamicDst);

        var sourceSlot = source.fromNetworkSlot(action.getSource().getSlot());
        var destinationSlot = destination.fromNetworkSlot(action.getDestination().getSlot());
        var sourceItem = source.getItem(sourceSlot);
        var destinationItem = destination.getItem(destinationSlot);
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
                new ItemStackResponseContainerInfo(
                        source.getContainerEnumName(sourceSlot),
                        Lists.newArrayList(
                                new ItemStackResponseSlotInfo(
                                        source.toNetworkSlot(sourceSlot),
                                        source.toNetworkSlot(sourceSlot),
                                        destinationItem.getCount(),
                                        destinationItem.getNetId(),
                                        destinationItem.getCustomName(),
                                        destinationItem.getDamage(),
                                        ""
                                )
                        ),
                        sourceContainerName
                ),
                new ItemStackResponseContainerInfo(
                        destination.getContainerEnumName(destinationSlot),
                        Lists.newArrayList(
                                new ItemStackResponseSlotInfo(
                                        destination.toNetworkSlot(destinationSlot),
                                        destination.toNetworkSlot(destinationSlot),
                                        sourceItem.getCount(),
                                        sourceItem.getNetId(),
                                        sourceItem.getCustomName(),
                                        sourceItem.getDamage(),
                                        ""
                                )
                        ),
                        destinationContainerName
                )
        ));
    }
}
