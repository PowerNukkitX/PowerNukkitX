package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class PlayerToggleFlightEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected final boolean isFlying;
    /**
     * @deprecated 
     */
    

    public PlayerToggleFlightEvent(Player player, boolean isFlying) {
        this.player = player;
        this.isFlying = isFlying;
    }
    /**
     * @deprecated 
     */
    

    public boolean isFlying() {
        return this.isFlying;
    }
}
