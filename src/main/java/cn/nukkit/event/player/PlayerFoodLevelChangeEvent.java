package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

public class PlayerFoodLevelChangeEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected int foodLevel;
    protected float foodSaturationLevel;
    /**
     * @deprecated 
     */
    

    public PlayerFoodLevelChangeEvent(Player player, int foodLevel, float foodSaturationLevel) {
        this.player = player;
        this.foodLevel = foodLevel;
        this.foodSaturationLevel = foodSaturationLevel;
    }
    /**
     * @deprecated 
     */
    

    public int getFoodLevel() {
        return this.foodLevel;
    }
    /**
     * @deprecated 
     */
    

    public void setFoodLevel(int foodLevel) {
        this.foodLevel = foodLevel;
    }
    /**
     * @deprecated 
     */
    

    public float getFoodSaturationLevel() {
        return foodSaturationLevel;
    }
    /**
     * @deprecated 
     */
    

    public void setFoodSaturationLevel(float foodSaturationLevel) {
        this.foodSaturationLevel = foodSaturationLevel;
    }
}
