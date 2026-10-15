package org.powernukkitx.inventory.request;

import org.cloudburstmc.protocol.bedrock.data.payload.common.RedactableString;
import org.cloudburstmc.protocol.bedrock.data.payload.inventory.net.ItemStackNetId;
import org.powernukkitx.Player;
import org.powernukkitx.inventory.CreativeOutputInventory;
import org.powernukkitx.inventory.Inventory;
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
        if (sourceSlot < 0 || sourceSlot >= source.getSize() || destinationSlot < 0 || destinationSlot >= destination.getSize()) {
            log.warn("swap action points outside of the containers it addresses");
            return context.error();
        }
        if (source == destination && sourceSlot == destinationSlot) {
            log.warn("cannot swap a slot with itself!");
            return context.error();
        }
        // The creative output is a preview the server writes, not a slot the client owns.
        if (source instanceof CreativeOutputInventory || destination instanceof CreativeOutputInventory) {
            log.warn("cannot swap with the creative output!");
            return context.error();
        }

        var sourceItem = source.getItem(sourceSlot);
        var destinationItem = destination.getItem(destinationSlot);
        if (sourceItem.isNull() && destinationItem.isNull()) {
            log.warn("cannot swap two empty slots!");
            return context.error();
        }
        if (validateStackNetworkId(sourceItem.getNetId(), action.getSource().getStackNetworkId())) {
            log.warn("mismatch stack network id!");
            return context.error();
        }
        if (validateStackNetworkId(destinationItem.getNetId(), action.getDestination().getStackNetworkId())) {
            log.warn("mismatch stack network id!");
            return context.error();
        }
        if (!destination.setItem(destinationSlot, sourceItem, false)) {
            return context.error();
        }
        if (!source.setItem(sourceSlot, destinationItem, false)) {
            destination.setItem(destinationSlot, destinationItem, false);
            return context.error();
        }
        return context.success(List.of(
            new ItemStackResponseContainerInfo(
                source.getContainerEnumName(sourceSlot),
                Lists.newArrayList(
                    new ItemStackResponseSlotInfo(
                        source.toNetworkSlot(sourceSlot),
                        source.toNetworkSlot(sourceSlot),
                        destinationItem.getCount(),
                        new ItemStackNetId(destinationItem.getNetId()),
                        new RedactableString(destinationItem.getCustomName(), ""),
                        destinationItem.getDamage()
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
                        new ItemStackNetId(sourceItem.getNetId()),
                        new RedactableString(sourceItem.getCustomName(), ""),
                        sourceItem.getDamage()
                    )
                ),
                destinationContainerName
            )
        ));
    }
}
