package org.powernukkitx.event.level;

import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.level.format.IChunk;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ChunkUnloadEvent extends ChunkEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public ChunkUnloadEvent(IChunk chunk) {
        super(chunk);
    }

}
