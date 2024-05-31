package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.HandlerList;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockSpreadEvent extends BlockFormEvent {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Block source;
    /**
     * @deprecated 
     */
    

    public BlockSpreadEvent(Block block, Block source, Block newState) {
        super(block, newState);
        this.source = source;
    }

    public Block getSource() {
        return source;
    }
}
