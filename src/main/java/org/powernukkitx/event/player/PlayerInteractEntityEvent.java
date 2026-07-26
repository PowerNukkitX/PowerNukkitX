package org.powernukkitx.event.player;

import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.Vector3;

/**
 * @author CreeperFace
 * @since 1. 1. 2017
 */
public class PlayerInteractEntityEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected final Entity entity;
    protected final Item item;
    protected final Vector3 clickedPos;

    public PlayerInteractEntityEvent(Player player, Entity entity, Item item, Vector3 clickedPos) {
        this.player = player;
        this.entity = entity;
        this.item = item;
        this.clickedPos = clickedPos;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public Item getItem() {
        return this.item;
    }

    public Vector3 getClickedPos() {
        return clickedPos;
    }

}
