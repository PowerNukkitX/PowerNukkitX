package cn.nukkit.event.level;

import cn.nukkit.level.format.IChunk;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class ChunkEvent extends LevelEvent {

    private final IChunk chunk;

    public ChunkEvent(IChunk chunk) {
        super(chunk.getProvider().getLevel());
        this.chunk = chunk;
    }

    public IChunk getChunk() {
        return chunk;
    }
}
