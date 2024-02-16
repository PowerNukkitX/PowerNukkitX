package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;


public class SignWaxedEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Player player;
    private final boolean waxed;

    public SignWaxedEvent(Block block, Player player, boolean waxed) {
        super(block);
        this.player = player;
        this.waxed = waxed;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isWaxed() {
        return waxed;
    }
}