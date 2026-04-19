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
    protected String description;

    public PlayerHackDetectedEvent(Player player, HackType type) {
        this(player, type, "");
    }

    public PlayerHackDetectedEvent(Player player, HackType type, String description) {
        this.player = player;
        this.type = type;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public HackType getType() {
        return type;
    }

    public boolean isKick() {
        return kick;
    }

    public void setKick(boolean kick) {
        this.kick = kick;
    }

    public enum HackType {
        FLIGHT,
        SPEED,
        COMMAND_SPAM,
        MODAL_SPAM,
        PACKET_FLOOD,
        PERMISSION_REQUEST,
        INVALID_PVP,
        INVALID_PVE,
        NOCLIP,
        FAST_BREAK,
        TIMER,
        AUTOCLICKER,
        PHASE,
        GHOSTHAND,
        OTHER
    }
}
