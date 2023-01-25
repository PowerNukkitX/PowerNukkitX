package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * 方块改变后会触发
 */
public class BlockChangeEvent extends BlockEvent implements Cancellable {
    protected final Block after;

    public BlockChangeEvent(Block before, Block after) {
        super(before);
        this.after = after;
    }

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    /**
     * 获取改变之后的方块
     *
     * @return the after block
     */
    public Block getAfterBlock() {
        return after;
    }
}
