package cn.nukkit.event.level;

import cn.nukkit.level.format.FullChunk;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ChunkPopulateEvent extends ChunkEvent {

    public ChunkPopulateEvent(FullChunk chunk) {
        super(chunk);
    }
}
