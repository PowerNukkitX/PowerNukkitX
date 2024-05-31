package cn.nukkit.event.level;

import cn.nukkit.event.HandlerList;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class SpawnChangeEvent extends LevelEvent {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Position previousSpawn;
    /**
     * @deprecated 
     */
    

    public SpawnChangeEvent(Level level, Position previousSpawn) {
        super(level);
        this.previousSpawn = previousSpawn;
    }

    public Position getPreviousSpawn() {
        return previousSpawn;
    }
}
