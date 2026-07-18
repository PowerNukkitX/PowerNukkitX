package org.powernukkitx.event.block;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockLiquid;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;

public class LiquidFlowEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Block to;
    private final BlockLiquid source;
    private final int newFlowDecay;

    public LiquidFlowEvent(Block to, BlockLiquid source, int newFlowDecay) {
        super(to);
        this.to = to;
        this.source = source;
        this.newFlowDecay = newFlowDecay;
    }

    public int getNewFlowDecay() {
        return this.newFlowDecay;
    }

    public BlockLiquid getSource() {
        return this.source;
    }

    public Block getTo() {
        return this.to;
    }
}
