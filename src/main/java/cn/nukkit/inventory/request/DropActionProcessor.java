package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.itemstack.request.action.DropAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestActionType;
import cn.nukkit.network.protocol.types.itemstack.response.ItemStackResponseContainer;
import cn.nukkit.network.protocol.types.itemstack.response.ItemStackResponseSlot;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

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
        Inventory inventory = NetworkMapping.getInventory(player, action.getSource().getContainer());
        var count = action.getCount();
        var slot = inventory.fromNetworkSlot(action.getSource().getSlot());
        var item = inventory.getItem(slot);

        PlayerDropItemEvent ev;
        player.getServer().getPluginManager().callEvent(ev = new PlayerDropItemEvent(player, item));
        if (ev.isCancelled()) {
            return context.error();
        }

        if (validateStackNetworkId(item.getNetId(), action.getSource().getStackNetworkId())) {
            log.warn("mismatch stack network id!");
            return context.error();
        }
        if (item.isNull()) {
            log.warn("cannot throw an air!");
            return context.error();
        }
        if (item.getCount() < count) {
            log.warn("cannot throw more items than the current amount!");
            return context.error();
        }
        Item drop = item.clone();
        drop.setCount(count);
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
                new ItemStackResponseContainer(
                        inventory.getSlotType(slot),
                        Lists.newArrayList(
                                new ItemStackResponseSlot(
                                        inventory.toNetworkSlot(slot),
                                        inventory.toNetworkSlot(slot),
                                        item.getCount(),
                                        item.getNetId(),
                                        item.getCustomName(),
                                        item.getDamage()
                                )
                        )
                )
        ));
    }
}
