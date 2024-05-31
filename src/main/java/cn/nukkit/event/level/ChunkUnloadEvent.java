package cn.nukkit.event.level;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.format.IChunk;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ChunkUnloadEvent extends ChunkEvent implements Cancellable {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }
    /**
     * @deprecated 
     */
    

    public ChunkUnloadEvent(IChunk chunk) {
        super(chunk);
    }

}
