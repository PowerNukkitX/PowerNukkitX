package cn.nukkit.event.redstone;

import cn.nukkit.block.Block;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.block.BlockUpdateEvent;
import lombok.Getter;

/**
 * @author Angelic47 (Nukkit Project)
 */
public class RedstoneUpdateEvent extends BlockUpdateEvent {

    @Getter
    private static final HandlerList handlers = new HandlerList();

    public RedstoneUpdateEvent(Block source) {
        super(source);
    }

}

