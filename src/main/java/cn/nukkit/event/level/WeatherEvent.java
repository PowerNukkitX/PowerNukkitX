package cn.nukkit.event.level;

import cn.nukkit.event.Event;
import cn.nukkit.level.Level;

/**
 * @author funcraft (Nukkit Project)
 */
public abstract class WeatherEvent extends Event {

    private final Level level;
    /**
     * @deprecated 
     */
    

    public WeatherEvent(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }
}
