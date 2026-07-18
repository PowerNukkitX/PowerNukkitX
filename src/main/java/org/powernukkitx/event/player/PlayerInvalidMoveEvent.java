package org.powernukkitx.event.player;

import org.powernukkitx.Player;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;

/**
 * call when a player moves wrongly
 *
 * @author WilliamGao
 */

public class PlayerInvalidMoveEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private boolean revert;

    public PlayerInvalidMoveEvent(Player player, boolean revert) {
        this.player = player;
        this.revert = revert;
    }

    public boolean isRevert() {
        return this.revert;
    }

}
