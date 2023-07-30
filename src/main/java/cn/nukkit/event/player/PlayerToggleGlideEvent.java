package cn.nukkit.event.player;

import cn.nukkit.event.Cancellable;
import cn.nukkit.player.Player;

public class PlayerToggleGlideEvent extends PlayerEvent implements Cancellable {

    protected final boolean isGliding;

    public PlayerToggleGlideEvent(Player player, boolean isSneaking) {
        this.player = player;
        this.isGliding = isSneaking;
    }

    public boolean isGliding() {
        return this.isGliding;
    }
}
