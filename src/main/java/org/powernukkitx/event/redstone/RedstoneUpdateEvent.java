package org.powernukkitx.event.redstone;

import org.powernukkitx.block.Block;
import org.powernukkitx.event.HandlerList;
import org.powernukkitx.event.block.BlockUpdateEvent;

/**
 * @author Angelic47 (Nukkit Project)
 */
public class RedstoneUpdateEvent extends BlockUpdateEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public RedstoneUpdateEvent(Block source) {
        super(source);
    }

}

