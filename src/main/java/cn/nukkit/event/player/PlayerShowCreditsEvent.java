package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
/**
 * @author GoodLucky777
 */
public class PlayerShowCreditsEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }
    /**
     * @deprecated 
     */
    

    public PlayerShowCreditsEvent(Player player) {
        this.player = player;
    }
}
