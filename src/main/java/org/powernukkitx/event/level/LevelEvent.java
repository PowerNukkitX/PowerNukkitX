package org.powernukkitx.event.level;

import org.powernukkitx.event.Event;
import org.powernukkitx.level.Level;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class LevelEvent extends Event {

    private final Level level;

    public LevelEvent(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }
}
