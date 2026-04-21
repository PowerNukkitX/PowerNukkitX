package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.inventory.HumanInventory;
import cn.nukkit.item.Item;
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
        return ItemStackRequestActionType.MINE_BLOCK;
    }

    @Nullable
    @Override
    public ActionResponse handle(MineBlockAction action, Player player, ItemStackRequestContext context) {
        HumanInventory inventory = player.getInventory();
        int heldItemIndex = inventory.getHeldItemIndex();
        if (heldItemIndex != action.getHotbarSlot()) {
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
            inventorySlotPacket.setSlot(action.getHotbarSlot());
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
                                        itemInHand.getNetId(),
                                        itemInHand.getCustomName(),
                                        itemInHand.getDamage(),
                                        ""
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
