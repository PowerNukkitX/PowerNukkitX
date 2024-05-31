package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class PlayerAchievementAwardedEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected final String achievement;
    /**
     * @deprecated 
     */
    

    public PlayerAchievementAwardedEvent(Player player, String achievementId) {
        this.player = player;
        this.achievement = achievementId;
    }
    /**
     * @deprecated 
     */
    

    public String getAchievement() {
        return this.achievement;
    }
}
