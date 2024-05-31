package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.HandlerList;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockFormEvent extends BlockGrowEvent {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }
    /**
     * @deprecated 
     */
    

    public BlockFormEvent(Block block, Block newState) {
        super(block, newState);
    }

}
