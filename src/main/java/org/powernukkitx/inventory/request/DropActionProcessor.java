package org.powernukkitx.inventory.request;

import org.cloudburstmc.protocol.bedrock.data.payload.common.RedactableString;
import org.cloudburstmc.protocol.bedrock.data.payload.inventory.net.ItemStackNetId;
import org.powernukkitx.Player;
import org.powernukkitx.event.player.PlayerDropItemEvent;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.item.Item;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.inventory.FullContainerName;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.DropAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseContainerInfo;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseSlotInfo;

import java.util.List;

/**
 * Allay Project 2023/9/23
 *
 * @author daoge_cmd
 */
@Slf4j
public class DropActionProcessor implements ItemStackRequestActionProcessor<DropAction> {
    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.DROP;
    }

    @Override
    public ActionResponse handle(DropAction action, Player player, ItemStackRequestContext context) {
        FullContainerName containerName = action.getSource().getFullContainerName();
        Integer dynamicId = containerName.getDynamicID();
        Inventory inventory = NetworkMapping.getInventory(player, containerName.getContainerName(), dynamicId);
        var count = action.getAmount();
        var slot = inventory.fromNetworkSlot(action.getSource().getSlot());
        if (slot < 0 || slot >= inventory.getSize()) {
            log.warn("drop action points at slot {} which is outside of {}", slot, inventory.getClass().getSimpleName());
            return context.error();
        }
        var item = inventory.getItem(slot);

        if (validateStackNetworkId(item.getNetId(), action.getSource().getStackNetworkId())) {
            log.warn("mismatch stack network id!");
            return context.error();
        }
        if (item.isNull()) {
            log.warn("cannot throw an air!");
            return context.error();
        }
        if (count <= 0) {
            log.warn("cannot throw a non positive amount of items!");
            return context.error();
        }
        if (item.getCount() < count) {
            log.warn("cannot throw more items than the current amount!");
            return context.error();
        }

        Item drop = item.clone();
        drop.setCount(count);

        // The event has to see what actually leaves the inventory, not the whole stack it comes from.
        PlayerDropItemEvent ev;
        player.getServer().getPluginManager().callEvent(ev = new PlayerDropItemEvent(player, drop));
        if (ev.isCancelled()) {
            return context.error();
        }

        player.dropItem(drop);

        int c = item.getCount() - count;
        if (c <= 0) {
            inventory.clear(slot, false);
            item = inventory.getItem(slot);
        } else {
            item.setCount(c);
            inventory.setItem(slot, item, false);
        }
        return context.success(List.of(
            new ItemStackResponseContainerInfo(
                inventory.getContainerEnumName(slot),
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
