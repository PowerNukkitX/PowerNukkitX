package cn.nukkit.event.level;

import cn.nukkit.event.HandlerList;
import cn.nukkit.level.format.IChunk;
import lombok.Getter;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ChunkLoadEvent extends ChunkEvent {

    @Getter
    private static final HandlerList handlers = new HandlerList();

    private final boolean newChunk;

    public ChunkLoadEvent(IChunk chunk, boolean newChunk) {
        super(chunk);
        this.newChunk = newChunk;
    }

    public boolean isNewChunk() {
        return newChunk;
    }
}
