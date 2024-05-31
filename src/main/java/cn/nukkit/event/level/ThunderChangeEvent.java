package cn.nukkit.event.level;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Level;

/**
 * @author funcraft (Nukkit Project)
 */
public class ThunderChangeEvent extends WeatherEvent implements Cancellable {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final boolean to;
    /**
     * @deprecated 
     */
    

    public ThunderChangeEvent(Level level, boolean to) {
        super(level);
        this.to = to;
    }

    /**
     * Gets the state of thunder that the world is being set to
     *
     * @return true if the thunder is being set to start, false otherwise
     */
    /**
     * @deprecated 
     */
    
    public boolean toThunderState() {
        return to;
    }

}
