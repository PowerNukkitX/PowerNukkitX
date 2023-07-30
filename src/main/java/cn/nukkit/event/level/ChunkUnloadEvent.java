package cn.nukkit.event.level;

import cn.nukkit.event.Cancellable;
import cn.nukkit.level.format.FullChunk;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ChunkUnloadEvent extends ChunkEvent implements Cancellable {

    public ChunkUnloadEvent(FullChunk chunk) {
        super(chunk);
    }
}
