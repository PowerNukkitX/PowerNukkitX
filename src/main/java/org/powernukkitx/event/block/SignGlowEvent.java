package org.powernukkitx.event.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;

public class SignGlowEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Player player;
    private final boolean glowing;

    public SignGlowEvent(Block block, Player player, boolean glowing) {
        super(block);
        this.player = player;
        this.glowing = glowing;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isGlowing() {
        return this.glowing;
    }
}