package org.powernukkitx.event.player;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.event.HandlerList;

public class PlayerBedLeaveEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Block bed;

    public PlayerBedLeaveEvent(Player player, Block bed) {
        this.player = player;
        this.bed = bed;
    }

    public Block getBed() {
        return bed;
    }
}
