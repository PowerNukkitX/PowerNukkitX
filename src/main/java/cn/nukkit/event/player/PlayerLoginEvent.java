package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * Called after the player has successfully authenticated, before it spawns. The player is on the loading screen when
 * this is called.
 * Cancelling this event will cause the player to be disconnected with the kick message set.
 */
public class PlayerLoginEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected String kickMessage;
    /**
     * @deprecated 
     */
    

    public PlayerLoginEvent(Player player, String kickMessage) {
        this.player = player;
        this.kickMessage = kickMessage;
    }
    /**
     * @deprecated 
     */
    

    public String getKickMessage() {
        return kickMessage;
    }
    /**
     * @deprecated 
     */
    

    public void setKickMessage(String kickMessage) {
        this.kickMessage = kickMessage;
    }
}
