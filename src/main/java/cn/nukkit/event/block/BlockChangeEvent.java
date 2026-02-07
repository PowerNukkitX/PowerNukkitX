package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import lombok.Getter;

/**
 * Called when a block changes at a specific position in the world.
 * <p>
 * This event is triggered when a block is replaced with another block
 * through the level logic (for example via block updates, physics,
 * or internal game mechanics).
 * <p>
 * The event provides both the new block and the previous block state,
 * allowing plugins to observe or cancel the block change before it is finalized.
 * <p>This event is not tied to player actions and may be triggered
 * by various internal systems.
 */
public class BlockChangeEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    /** The previous block state before the change occurred. */
    @Getter
    private final Block previousBlock;

    public static HandlerList getHandlers() {
        return handlers;
    }

    /**
     * @param block The new block that is being set at this position.
     * @param previousBlock The block that previously occupied this position.
     */
    public BlockChangeEvent(Block block, Block previousBlock) {
        super(block);
        this.previousBlock = previousBlock;
    }
}