package cn.nukkit.event.level;

import cn.nukkit.event.HandlerList;
import cn.nukkit.level.format.IChunk;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ChunkLoadEvent extends ChunkEvent {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final boolean newChunk;
    /**
     * @deprecated 
     */
    

    public ChunkLoadEvent(IChunk chunk, boolean newChunk) {
        super(chunk);
        this.newChunk = newChunk;
    }
    /**
     * @deprecated 
     */
    

    public boolean isNewChunk() {
        return newChunk;
    }
}
