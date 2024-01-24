package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import lombok.Getter;

/**
 * @author Extollite (Nukkit Project)
 */

public class PlayerLocallyInitializedEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public PlayerLocallyInitializedEvent(Player player) {
        this.player = player;
    }
}
