package cn.nukkit.event.level;

import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Level;
import lombok.Getter;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class LevelInitEvent extends LevelEvent {

    @Getter
    private static final HandlerList handlers = new HandlerList();

    public LevelInitEvent(Level level) {
        super(level);
    }

}
