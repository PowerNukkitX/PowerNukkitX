package cn.nukkit.event.redstone;

import cn.nukkit.block.Block;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.block.BlockUpdateEvent;

/**
 * @author Angelic47 (Nukkit Project)
 */
public class RedstoneUpdateEvent extends BlockUpdateEvent {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }
    /**
     * @deprecated 
     */
    

    public RedstoneUpdateEvent(Block source) {
        super(source);
    }

}

