package org.powernukkitx.event.level;

import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.level.Level;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class LevelUnloadEvent extends LevelEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public LevelUnloadEvent(Level level) {
        super(level);
    }

}
