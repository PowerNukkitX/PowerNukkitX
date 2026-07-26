package org.powernukkitx.event.level;

import org.powernukkitx.event.HandlerList;
import org.powernukkitx.level.Level;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class LevelInitEvent extends LevelEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public LevelInitEvent(Level level) {
        super(level);
    }

}
