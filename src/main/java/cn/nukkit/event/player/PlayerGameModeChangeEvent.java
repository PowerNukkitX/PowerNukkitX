package cn.nukkit.event.player;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class PlayerGameModeChangeEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected final int gamemode;
    protected AdventureSettings newAdventureSettings;
    /**
     * @deprecated 
     */
    

    public PlayerGameModeChangeEvent(Player player, int newGameMode, AdventureSettings newAdventureSettings) {
        this.player = player;
        this.gamemode = newGameMode;
        this.newAdventureSettings = newAdventureSettings;
    }
    /**
     * @deprecated 
     */
    

    public int getNewGamemode() {
        return gamemode;
    }

    public AdventureSettings getNewAdventureSettings() {
        return newAdventureSettings;
    }
    /**
     * @deprecated 
     */
    

    public void setNewAdventureSettings(AdventureSettings newAdventureSettings) {
        this.newAdventureSettings = newAdventureSettings;
    }
}
