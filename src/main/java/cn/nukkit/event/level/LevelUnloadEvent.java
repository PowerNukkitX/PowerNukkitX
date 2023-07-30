package cn.nukkit.event.level;

import cn.nukkit.event.Cancellable;
import cn.nukkit.level.Level;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class LevelUnloadEvent extends LevelEvent implements Cancellable {

    public LevelUnloadEvent(Level level) {
        super(level);
    }
}
