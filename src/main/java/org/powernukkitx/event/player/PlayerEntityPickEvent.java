package org.powernukkitx.event.player;

import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.item.Item;

/**
 * @author Kaooot
 */
public class PlayerEntityPickEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Entity entityClicked;
    private Item item;

    public PlayerEntityPickEvent(Player player, Entity entityClicked, Item item) {
        this.player = player;
        this.entityClicked = entityClicked;
        this.item = item;
    }

    public Item getItem() {
        return this.item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Entity getEntityClicked() {
        return this.entityClicked;
    }
}