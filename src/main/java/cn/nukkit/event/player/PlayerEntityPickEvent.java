package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;

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