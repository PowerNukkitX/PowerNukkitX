package cn.nukkit.event.level;

import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Level;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class LevelLoadEvent extends LevelEvent {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }
    /**
     * @deprecated 
     */
    

    public LevelLoadEvent(Level level) {
        super(level);
    }

}
