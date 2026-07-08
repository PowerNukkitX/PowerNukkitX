package org.powernukkitx.event.player;

import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.item.Item;

public class PlayerChangeArmorStandEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Entity armorStand;
    private final int slot;
    private Item item;

    public PlayerChangeArmorStandEvent(Player player, Entity armorStand, Item item, int slot) {
        this.player = player;
        this.armorStand = armorStand;
        this.item = item;
        this.slot = slot;
    }

    public Entity getArmorStand() {
        return armorStand;
    }

    public int getSlot() {
        return slot;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
