package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.inventory.BeaconInventory;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.FullContainerName;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.DestroyAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseContainerInfo;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseSlotInfo;

import java.util.List;

import static cn.nukkit.inventory.request.CraftResultDeprecatedActionProcessor.NO_RESPONSE_DESTROY_KEY;


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
        if (player.getGamemode() != Player.CREATIVE && !(sourceInventory instanceof BeaconInventory)) {
            log.warn("only creative mode can destroy item");
            return context.error();
        }
        var count = action.getCount();
        var slot = sourceInventory.fromNetworkSlot(action.getSource().getSlot());
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
        return context.success(List.of(
                new ItemStackResponseContainerInfo(
                        sourceInventory.getContainerEnumName(slot),
                        Lists.newArrayList(
                                new ItemStackResponseSlotInfo(
                                        sourceInventory.toNetworkSlot(slot),
                                        sourceInventory.toNetworkSlot(slot),
                                        item.getCount(),
                                        item.getNetId(),
                                        item.getCustomName(),
                                        item.getDamage(),
                                        ""
                                )
                        ),
                        containerName
                )
        ));
    }
}
