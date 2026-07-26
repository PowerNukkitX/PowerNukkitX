package org.powernukkitx.inventory.fake;

import org.powernukkitx.event.inventory.ItemStackRequestActionEvent;
import org.powernukkitx.item.Item;

@FunctionalInterface
public interface ItemHandler {
    void handle(FakeInventory fakeInventory, int slot, Item oldItem, Item newItem, ItemStackRequestActionEvent event);
}
