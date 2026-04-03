package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;

import java.util.LinkedHashSet;
import java.util.Set;

public final class InventoryObserverSync {
    public static void syncOtherViewers(Player actor, Inventory inventory, int... slots) {
        if (inventory == null) {
            return;
        }

        Set<Player> viewers = inventory.getViewers();
        if (viewers == null || viewers.isEmpty()) {
            return;
        }

        LinkedHashSet<Player> observers = new LinkedHashSet<>();
        for (Player viewer : viewers) {
            if (viewer != null && viewer != actor) {
                observers.add(viewer);
            }
        }

        if (observers.isEmpty()) {
            return;
        }

        inventory.sendContents(observers);
    }
}