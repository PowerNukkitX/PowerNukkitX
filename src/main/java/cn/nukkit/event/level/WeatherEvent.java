package cn.nukkit.event.level;

import cn.nukkit.event.Event;
import cn.nukkit.level.Dimension;

/**
 * @author funcraft (Nukkit Project)
 */
public abstract class WeatherEvent extends Event {

    private final Dimension level;

    public WeatherEvent(Dimension level) {
        this.level = level;
    }

    public Dimension getLevel() {
        return level;
    }
}
