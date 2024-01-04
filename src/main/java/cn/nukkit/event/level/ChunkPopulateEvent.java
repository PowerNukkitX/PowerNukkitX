package cn.nukkit.event.level;

import cn.nukkit.event.HandlerList;
import cn.nukkit.level.format.IChunk;
import lombok.Getter;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ChunkPopulateEvent extends ChunkEvent {

    @Getter
    private static final HandlerList handlers = new HandlerList();

    public ChunkPopulateEvent(IChunk chunk) {
        super(chunk);
    }

}
