package org.powernukkitx.inventory.request;

import org.cloudburstmc.protocol.bedrock.data.payload.common.RedactableString;
import org.cloudburstmc.protocol.bedrock.data.payload.inventory.net.ItemStackNetId;
import org.powernukkitx.Player;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.item.Item;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.FullContainerName;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.DestroyAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseContainerInfo;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseSlotInfo;

import java.util.List;

import static org.powernukkitx.inventory.request.CraftResultDeprecatedActionProcessor.NO_RESPONSE_DESTROY_KEY;


/**
 * Allay Project 2023/7/28
 *
 * @author daoge_cmd
 */
@Slf4j
public class DestroyActionProcessor implements ItemStackRequestActionProcessor<DestroyAction> {
    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.DESTROY;
    }

    @Override
    public ActionResponse handle(DestroyAction action, Player player, ItemStackRequestContext context) {
        Boolean noResponseForDestroyAction = context.get(NO_RESPONSE_DESTROY_KEY);
        if (noResponseForDestroyAction != null && noResponseForDestroyAction) {
            return null;
        }

        FullContainerName containerName = action.getSource().getFullContainerName();
        Integer dynamicId = containerName.getDynamicID();
        ContainerEnumName container = containerName.getContainerName();
        var sourceInventory = NetworkMapping.getInventory(player, container, dynamicId);
        var slot = sourceInventory.fromNetworkSlot(action.getSource().getSlot());
        if (slot < 0 || slot >= sourceInventory.getSize()) {
            log.warn("destroy action points at slot {} which is outside of {}", slot, sourceInventory.getClass().getSimpleName());
            return context.error();
        }

        ContainerEnumName slotType = resolveSlotType(sourceInventory, slot);
        if (slotType == null) {
            log.warn("unknown slot type for slot {} in inventory {}", slot, sourceInventory.getClass().getSimpleName());
            return context.error();
        }

        // Slots a station already emptied in this request (the beacon payment, for one) only need the
        // destroy acknowledged, not applied again.
        if (context.isServerConsumed(slotType)) {
            return respondWithSlot(sourceInventory, slot, slotType, containerName, context);
        }

        if (player.getGamemode() != Player.CREATIVE) {
            log.warn("only creative mode can destroy item");
            return context.error();
        }
        var count = action.getAmount();
        var item = sourceInventory.getItem(slot);
        if (validateStackNetworkId(item.getNetId(), action.getSource().getStackNetworkId())) {
            log.warn("mismatch stack network id!");
            return context.error();
        }
        if (item.isNull()) {
            log.warn("cannot destroy an air!");
            return context.error();
        }
        if (item.getCount() < count) {
            log.warn("cannot destroy more items than the current amount!");
            return context.error();
        }
        if (item.getCount() > count) {
            item.setCount(item.getCount() - count);
            sourceInventory.setItem(slot, item, false);
        } else {
            sourceInventory.clear(slot, false);
            item = sourceInventory.getItem(slot);
        }
        return respondWithSlot(sourceInventory, slot, slotType, containerName, context);
    }

    private ActionResponse respondWithSlot(Inventory inventory, int slot, ContainerEnumName slotType, FullContainerName containerName, ItemStackRequestContext context) {
        Item item = inventory.getItem(slot);
        return context.success(List.of(
            new ItemStackResponseContainerInfo(
                slotType,
                Lists.newArrayList(
                    new ItemStackResponseSlotInfo(
                        inventory.toNetworkSlot(slot),
                        inventory.toNetworkSlot(slot),
                        item.getCount(),
                        new ItemStackNetId(item.getNetId()),
                        new RedactableString(item.getCustomName(), ""),
                        item.getDamage()
                    )
                ),
                containerName
            )
        ));
    }
}
