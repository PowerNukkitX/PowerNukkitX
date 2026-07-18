package org.powernukkitx.event.player;

import org.powernukkitx.Player;
import org.powernukkitx.event.Event;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class PlayerEvent extends Event {
    protected Player player;

    public Player getPlayer() {
        return player;
    }
}
