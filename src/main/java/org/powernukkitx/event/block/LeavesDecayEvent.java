package org.powernukkitx.event.block;

import org.powernukkitx.block.Block;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class LeavesDecayEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public LeavesDecayEvent(Block block) {
        super(block);
    }

}
