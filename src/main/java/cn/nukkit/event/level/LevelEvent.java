package cn.nukkit.event.level;

import cn.nukkit.event.Event;
import cn.nukkit.level.Dimension;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class LevelEvent extends Event {

    private final Dimension level;

    public LevelEvent(Dimension level) {
        this.level = level;
    }

    public Dimension getLevel() {
        return level;
    }
}
