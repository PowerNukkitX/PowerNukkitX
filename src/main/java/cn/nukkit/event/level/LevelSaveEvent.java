package cn.nukkit.event.level;

import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Level;
import lombok.Getter;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class LevelSaveEvent extends LevelEvent {

    @Getter
    private static final HandlerList handlers = new HandlerList();

    public LevelSaveEvent(Level level) {
        super(level);
    }

}
