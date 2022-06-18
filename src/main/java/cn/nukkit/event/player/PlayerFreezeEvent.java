package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.event.Cancellable;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class PlayerFreezeEvent extends PlayerEvent implements Cancellable {
    private float speedFactor;
    private float baseSpeed;

    public PlayerFreezeEvent(Player player, float speedFactor, float baseSpeed) {
        this.player = player;
        this.speedFactor = speedFactor;
        this.baseSpeed = baseSpeed;
    }

    public void setBaseSpeed(float baseSpeed) {
        this.baseSpeed = baseSpeed;
    }

    public void setSpeedFactor(float speedFactor) {
        this.speedFactor = speedFactor;
    }

    public float getBaseSpeed() {
        return this.baseSpeed;
    }

    public float getSpeedFactor() {
        return this.speedFactor;
    }
}
