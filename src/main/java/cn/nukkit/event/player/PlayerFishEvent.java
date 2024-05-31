package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.entity.item.EntityFishingHook;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;

/**
 * An event that is called when player catches a fish
 *
 * @author PetteriM1
 */

public class PlayerFishEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final EntityFishingHook hook;
    private Item loot;
    private int experience;
    private Vector3 motion;
    /**
     * @deprecated 
     */
    

    public PlayerFishEvent(Player player, EntityFishingHook hook, Item loot, int experience, Vector3 motion) {
        this.player = player;
        this.hook = hook;
        this.loot = loot;
        this.experience = experience;
        this.motion = motion;
    }

    public EntityFishingHook getHook() {
        return hook;
    }

    public Item getLoot() {
        return loot;
    }
    /**
     * @deprecated 
     */
    

    public void setLoot(Item loot) {
        this.loot = loot;
    }
    /**
     * @deprecated 
     */
    

    public int getExperience() {
        return experience;
    }
    /**
     * @deprecated 
     */
    

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public Vector3 getMotion() {
        return motion;
    }
    /**
     * @deprecated 
     */
    

    public void setMotion(Vector3 motion) {
        this.motion = motion;
    }
}
