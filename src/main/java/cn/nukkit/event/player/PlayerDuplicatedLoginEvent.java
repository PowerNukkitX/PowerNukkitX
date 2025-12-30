package cn.nukkit.event.player;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;

public class PlayerDuplicatedLoginEvent extends PlayerEvent implements Cancellable {
    public PlayerDuplicatedLoginEvent(Player player) {
        this.player = player;
    }
}
