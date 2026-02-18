package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.inventory.HumanInventory;
import cn.nukkit.inventory.SpecialWindowId;
import cn.nukkit.item.Item;
import org.cloudburstmc.protocol.bedrock.packet.InventorySlotPacket;
import org.cloudburstmc.protocol.bedrock.data.inventory.FullContainerName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerSlotType;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.MineBlockAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseContainer;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseSlot;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
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

        Item itemInHand = inventory.getItemInHand();
        if (validateStackNetworkId(itemInHand.getNetId(), action.getStackNetworkId())) {
            log.warn("mismatch source stack network id!");
            return context.error();
        }

        if (itemInHand.getDamage() != action.getPredictedDurability()) {
            // Let regular inventory sync correct client state.
            inventory.sendSlot(heldItemIndex, player);
        }
        var itemStackResponseSlot =
                new ItemStackResponseContainer(
                        inventory.getSlotType(heldItemIndex),
                        Lists.newArrayList(
                                new ItemStackResponseSlot(
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
                                inventory.getSlotType(heldItemIndex),
                                0   // I don't know the purpose of the dynamicId yet, this is why I leave it at 0 for the MineBlockAction
                        )
                );
        return context.success(List.of(itemStackResponseSlot));
    }
}
