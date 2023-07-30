package cn.nukkit.event.player;

import cn.nukkit.api.Since;
import cn.nukkit.entity.item.EntityFishingHook;
import cn.nukkit.event.Cancellable;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.player.Player;

/**
 * An event that is called when player catches a fish
 *
 * @author PetteriM1
 */
@Since("1.5.0.0-PN")
public class PlayerFishEvent extends PlayerEvent implements Cancellable {

    private final EntityFishingHook hook;
    private Item loot;
    private int experience;
    private Vector3 motion;

    @Since("1.5.0.0-PN")
    public PlayerFishEvent(Player player, EntityFishingHook hook, Item loot, int experience, Vector3 motion) {
        this.player = player;
        this.hook = hook;
        this.loot = loot;
        this.experience = experience;
        this.motion = motion;
    }

    @Since("1.5.0.0-PN")
    public EntityFishingHook getHook() {
        return hook;
    }

    @Since("1.5.0.0-PN")
    public Item getLoot() {
        return loot;
    }

    @Since("1.5.0.0-PN")
    public void setLoot(Item loot) {
        this.loot = loot;
    }

    @Since("1.5.0.0-PN")
    public int getExperience() {
        return experience;
    }

    @Since("1.5.0.0-PN")
    public void setExperience(int experience) {
        this.experience = experience;
    }

    @Since("1.5.0.0-PN")
    public Vector3 getMotion() {
        return motion;
    }

    @Since("1.5.0.0-PN")
    public void setMotion(Vector3 motion) {
        this.motion = motion;
    }
}
