package org.powernukkitx.event.level;

import lombok.Getter;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;

@Getter
public class ChunkTickEvent extends LevelEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final long index;

    public ChunkTickEvent(Level level, long index) {
        super(level);
        this.index = index;
    }

    public IChunk getChunk() {
        return getLevel().getChunk(Level.getHashX(index), Level.getHashZ(index));
    }
}
