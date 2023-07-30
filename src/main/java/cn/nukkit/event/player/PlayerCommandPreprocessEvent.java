package cn.nukkit.event.player;

import cn.nukkit.event.Cancellable;
import cn.nukkit.player.Player;

public class PlayerCommandPreprocessEvent extends PlayerMessageEvent implements Cancellable {

    public PlayerCommandPreprocessEvent(Player player, String message) {
        this.player = player;
        this.message = message;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
