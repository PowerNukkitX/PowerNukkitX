package cn.nukkit.inventory.request;

import lombok.extern.slf4j.Slf4j;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.cloudburstmc.protocol.bedrock.data.GameType;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.DestroyAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseContainer;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseSlot;

import java.util.Collections;
import java.util.Map;

import static org.allaymc.api.container.Container.EMPTY_SLOT_PLACE_HOLDER;
import static org.allaymc.api.item.interfaces.ItemAirStack.AIR_TYPE;

/**
 * Allay Project 2023/7/28
 *
 * @author daoge_cmd
 */
@Slf4j
public class DestroyActionProcessor implements ContainerActionProcessor<DestroyAction> {
    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.DESTROY;
    }

    @Override
    public ActionResponse handle(DestroyAction action, EntityPlayer player, int currentActionIndex, ItemStackRequestAction[] actions, Map<Object, Object> dataPool) {
        if (player.getGameType() != GameType.CREATIVE) {
            log.warn("only creative mode can destroy item");
            return error();
        }
        var container = player.getContainerBySlotType(action.getSource().getContainer());
        var count = action.getCount();
        var slot = container.fromNetworkSlotIndex(action.getSource().getSlot());
        var item = container.getItemStack(slot);
        if (failToValidateStackNetworkId(item.getStackNetworkId(), action.getSource().getStackNetworkId())) {
            log.warn("mismatch stack network id!");
            return error();
        }
        if (item.getItemType() == AIR_TYPE) {
            log.warn("cannot destroy an air!");
            return error();
        }
        if (item.getCount() < count) {
            log.warn("cannot destroy more items than the current amount!");
            return error();
        }
        if (item.getCount() > count) {
            item.setCount(item.getCount() - count);
            container.onSlotChange(slot);
        } else {
            item = EMPTY_SLOT_PLACE_HOLDER;
            container.setItemStack(slot, item);
        }
        return new ActionResponse(
                true,
                Collections.singletonList(
                        new ItemStackResponseContainer(
                                container.getSlotType(slot),
                                Collections.singletonList(
                                        new ItemStackResponseSlot(
                                                container.toNetworkSlotIndex(slot),
                                                container.toNetworkSlotIndex(slot),
                                                item.getCount(),
                                                item.getStackNetworkId(),
                                                item.getCustomName(),
                                                item.getDurability()
                                        )
                                )
                        )
                )
        );
    }
}
