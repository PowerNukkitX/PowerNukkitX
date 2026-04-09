package cn.nukkit.event.level;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Dimension;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class LevelUnloadEvent extends LevelEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public LevelUnloadEvent(Dimension level) {
        super(level);
    }

}
