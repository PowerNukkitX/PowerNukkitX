package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class PlayerToggleSneakEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected final boolean isSneaking;
    /**
     * @deprecated 
     */
    

    public PlayerToggleSneakEvent(Player player, boolean isSneaking) {
        this.player = player;
        this.isSneaking = isSneaking;
    }
    /**
     * @deprecated 
     */
    

    public boolean isSneaking() {
        return this.isSneaking;
    }

}
