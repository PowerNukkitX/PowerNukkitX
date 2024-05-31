package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class PlayerExperienceChangeEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final int oldExp;
    private final int oldExpLevel;
    private int newExp;
    private int newExpLevel;
    /**
     * @deprecated 
     */
    

    public PlayerExperienceChangeEvent(Player player, int oldExp, int oldLevel, int newExp, int newLevel) {
        this.player = player;
        this.oldExp = oldExp;
        this.oldExpLevel = oldLevel;
        this.newExp = newExp;
        this.newExpLevel = newLevel;
    }
    /**
     * @deprecated 
     */
    

    public int getOldExperience() {
        return this.oldExp;
    }
    /**
     * @deprecated 
     */
    

    public int getOldExperienceLevel() {
        return this.oldExpLevel;
    }
    /**
     * @deprecated 
     */
    

    public int getNewExperience() {
        return this.newExp;
    }
    /**
     * @deprecated 
     */
    

    public void setNewExperience(int exp) {
        this.newExp = exp;
    }
    /**
     * @deprecated 
     */
    

    public int getNewExperienceLevel() {
        return this.newExpLevel;
    }
    /**
     * @deprecated 
     */
    

    public void setNewExperienceLevel(int level) {
        this.newExpLevel = level;
    }

}
