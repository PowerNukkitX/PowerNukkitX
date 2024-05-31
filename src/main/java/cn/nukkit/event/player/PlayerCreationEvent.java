package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class PlayerCreationEvent extends Event {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Class<? extends Player> playerClass;
    /**
     * @deprecated 
     */
    

    public PlayerCreationEvent(Class<? extends Player> playerClass) {
        this.playerClass = playerClass;
    }

    public Class<? extends Player> getPlayerClass() {
        return playerClass;
    }
    /**
     * @deprecated 
     */
    

    public void setPlayerClass(Class<? extends Player> playerClass) {
        this.playerClass = playerClass;
    }
}
