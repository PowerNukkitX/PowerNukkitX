package cn.nukkit.inventory.fake;

import cn.nukkit.event.inventory.ItemStackRequestActionEvent;
import cn.nukkit.item.Item;

@FunctionalInterface
public interface ItemHandler {
    void handle(FakeInventory fakeInventory, int slot, Item oldItem, Item newItem, ItemStackRequestActionEvent event);
}
