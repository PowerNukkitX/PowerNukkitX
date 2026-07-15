package org.powernukkitx.inventory.request;

import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.item.Item;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ConsumeAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.DestroyAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestAction;

import java.util.ArrayList;
import java.util.List;

public final class ConsumeActionHelper {

    private ConsumeActionHelper() {
    }

    public static void consume(Inventory inventory, int slot, int amount) {
        Item item = inventory.getItem(slot);
        int remaining = item.getCount() - amount;
        if (remaining <= 0) {
            inventory.clear(slot, false);
        } else {
            item.setCount(remaining);
            inventory.setItem(slot, item, false);
        }
    }

    public static List<ConsumeAction> findAllConsumeActions(ItemStackRequestAction[] actions, int startIndex) {
        var found = new ArrayList<ConsumeAction>();
        for (int i = startIndex; i < actions.length; i++) {
            var action = actions[i];
            if (action instanceof ConsumeAction consumeAction) {
                found.add(consumeAction);
            }
        }
        return found;
    }

    public static List<DestroyAction> findAllDestroyActions(ItemStackRequestAction[] actions, int startIndex) {
        var found = new ArrayList<DestroyAction>();
        for (int i = startIndex; i < actions.length; i++) {
            var action = actions[i];
            if (action instanceof DestroyAction destroyAction) {
                found.add(destroyAction);
            }
        }
        return found;
    }

}
