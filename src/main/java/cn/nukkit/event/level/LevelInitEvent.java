package cn.nukkit.event.level;

import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Dimension;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class LevelInitEvent extends LevelEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public LevelInitEvent(Dimension level) {
        super(level);
    }

}
