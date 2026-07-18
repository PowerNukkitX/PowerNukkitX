package org.powernukkitx.event.block;

import org.powernukkitx.block.Block;
import org.powernukkitx.event.HandlerList;


public class ConduitActivateEvent extends BlockEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public ConduitActivateEvent(Block block) {
        super(block);
    }

}
