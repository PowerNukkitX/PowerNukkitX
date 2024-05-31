package cn.nukkit.event.level;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Level;

/**
 * @author funcraft (Nukkit Project)
 */
public class WeatherChangeEvent extends WeatherEvent implements Cancellable {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final boolean to;
    /**
     * @deprecated 
     */
    

    public WeatherChangeEvent(Level level, boolean to) {
        super(level);
        this.to = to;
    }

    /**
     * Gets the state of weather that the world is being set to
     *
     * @return true if the weather is being set to raining, false otherwise
     */
    /**
     * @deprecated 
     */
    
    public boolean toWeatherState() {
        return to;
    }

}
