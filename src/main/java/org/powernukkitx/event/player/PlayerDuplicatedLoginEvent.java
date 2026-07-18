package org.powernukkitx.event.player;

import org.powernukkitx.Player;
import org.powernukkitx.event.Cancellable;

public class PlayerDuplicatedLoginEvent extends PlayerEvent implements Cancellable {
    public PlayerDuplicatedLoginEvent(Player player) {
        this.player = player;
    }
}
