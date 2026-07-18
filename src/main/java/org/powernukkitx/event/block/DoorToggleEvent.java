package org.powernukkitx.event.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.event.HandlerList;

/**
 * @author Snake1999
 * @since 2016/1/22
 */
public class DoorToggleEvent extends BlockUpdateEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Player player;

    public DoorToggleEvent(Block block, Player player) {
        super(block);
        this.player = player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
