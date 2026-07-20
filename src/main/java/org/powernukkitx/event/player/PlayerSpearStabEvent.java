package org.powernukkitx.event.player;

import org.powernukkitx.Player;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.item.Item;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

/**
 * Triggers before player stabs the spear.
 *
 * @author xRookieFight
 * @since 16/01/2026
 */
public class PlayerSpearStabEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    @Getter
    protected @Nullable final Item item;
    @Getter
    protected final float movementSpeed;

    public PlayerSpearStabEvent(Player player, @Nullable Item item, float movementSpeed) {
        this.player = player;
        this.item = item;
        this.movementSpeed = movementSpeed;
    }

}
