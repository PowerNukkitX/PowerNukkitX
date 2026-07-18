package org.powernukkitx.event.level;

import org.powernukkitx.event.HandlerList;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class SpawnChangeEvent extends LevelEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Position previousSpawn;

    public SpawnChangeEvent(Level level, Position previousSpawn) {
        super(level);
        this.previousSpawn = previousSpawn;
    }

    public Position getPreviousSpawn() {
        return previousSpawn;
    }
}
