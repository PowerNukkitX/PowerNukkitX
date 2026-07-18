package org.powernukkitx.event.block;

import org.powernukkitx.block.Block;
import org.powernukkitx.event.HandlerList;

/**
 * @author CreeperFace
 * @since 12.5.2017
 */
public class BlockRedstoneEvent extends BlockEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private int oldPower;
    private int newPower;

    public BlockRedstoneEvent(Block block, int oldPower, int newPower) {
        super(block);
        this.oldPower = oldPower;
        this.newPower = newPower;
    }

    public int getOldPower() {
        return oldPower;
    }

    public int getNewPower() {
        return newPower;
    }
}
