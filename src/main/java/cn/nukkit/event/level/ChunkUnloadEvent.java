package cn.nukkit.event.level;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.level.format.IChunk;
import lombok.Getter;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ChunkUnloadEvent extends ChunkEvent implements Cancellable {

    @Getter
    private static final HandlerList handlers = new HandlerList();

    public ChunkUnloadEvent(IChunk chunk) {
        super(chunk);
    }

}
