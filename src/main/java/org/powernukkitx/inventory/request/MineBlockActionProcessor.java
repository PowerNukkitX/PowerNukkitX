package org.powernukkitx.inventory.request;

import org.cloudburstmc.protocol.bedrock.data.payload.common.RedactableString;
import org.cloudburstmc.protocol.bedrock.data.payload.inventory.net.ItemStackNetId;
import org.powernukkitx.Player;
import org.powernukkitx.inventory.HumanInventory;
import org.powernukkitx.item.Item;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerId;
import org.cloudburstmc.protocol.bedrock.data.inventory.FullContainerName;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.MineBlockAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseContainerInfo;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseSlotInfo;
import org.cloudburstmc.protocol.bedrock.packet.InventorySlotPacket;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Slf4j
public class MineBlockActionProcessor implements ItemStackRequestActionProcessor<MineBlockAction> {
    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.SCREEN_HUD_MINE_BLOCK;
    }

    @Nullable
    @Override
    public ActionResponse handle(MineBlockAction action, Player player, ItemStackRequestContext context) {
        HumanInventory inventory = player.getInventory();
        int heldItemIndex = inventory.getHeldItemIndex();
        if (heldItemIndex != action.getSlot()) {
            log.warn("The held Item Index on the server side does not match the client side!");
            return context.error();
        }

        Item itemInHand = inventory.getItemInMainHand();
        if (validateStackNetworkId(itemInHand.getNetId(), action.getStackNetworkId())) {
            log.debug("Source stack network id mismatch!");
            return context.error();
        }

        if (itemInHand.getDamage() != action.getPredictedDurability()) {
            int id = ContainerId.INVENTORY;
            InventorySlotPacket inventorySlotPacket = new InventorySlotPacket();
            inventorySlotPacket.setContainerID(id);
            inventorySlotPacket.setSlot(action.getSlot());
            inventorySlotPacket.setItem(itemInHand.toNetwork());
            inventorySlotPacket.setFullContainerName(
                new FullContainerName(
                    ContainerEnumName.HOTBAR_CONTAINER,
                    id
                )
            );
            player.sendPacket(inventorySlotPacket);
        }
        var itemStackResponseSlot =
            new ItemStackResponseContainerInfo(
                inventory.getContainerEnumName(heldItemIndex),
                Lists.newArrayList(
                    new ItemStackResponseSlotInfo(
                        inventory.toNetworkSlot(heldItemIndex),
                        inventory.toNetworkSlot(heldItemIndex),
                        itemInHand.getCount(),
                        new ItemStackNetId(itemInHand.getNetId()),
                        new RedactableString(itemInHand.getCustomName(), ""),
                        itemInHand.getDamage()
                    )
                ),
                new FullContainerName(
                    inventory.getContainerEnumName(heldItemIndex),
                    null
                )
            );
        return context.success(List.of(itemStackResponseSlot));
    }
}
