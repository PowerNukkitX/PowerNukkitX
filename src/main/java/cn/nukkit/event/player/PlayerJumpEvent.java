package cn.nukkit.event.player;

import cn.nukkit.player.Player;

public class PlayerJumpEvent extends PlayerEvent {

    public PlayerJumpEvent(Player player) {
        this.player = player;
    }
}
