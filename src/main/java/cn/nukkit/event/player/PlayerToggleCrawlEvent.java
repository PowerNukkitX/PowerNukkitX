package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class PlayerToggleCrawlEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected final boolean isCrawling;

    public PlayerToggleCrawlEvent(Player player, boolean isSneaking) {
        this.player = player;
        this.isCrawling = isSneaking;
    }

    public boolean isCrawling() {
        return this.isCrawling;
    }

}
