package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;

public class PlayerHackDetectedEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected boolean kick = true;
    protected HackType type;

    public PlayerHackDetectedEvent(Player player, HackType type) {
        this.player = player;
        this.type = type;
    }

    public boolean isKick() {
        return kick;
    }

    public void setKick(boolean kick) {
        this.kick = kick;
    }

    public enum HackType {
        COMMAND_SPAM
    }
}
