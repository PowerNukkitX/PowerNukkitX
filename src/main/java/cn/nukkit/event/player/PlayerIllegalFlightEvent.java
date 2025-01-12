package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;

public class PlayerIllegalFlightEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    private boolean kick = true;

    public PlayerIllegalFlightEvent(Player player) {
        this.player = player;
    }

    public boolean isKick() {
        return kick;
    }

    public void setKick(boolean kick) {
        this.kick = kick;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }
}
