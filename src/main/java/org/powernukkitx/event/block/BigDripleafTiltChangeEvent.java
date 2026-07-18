package org.powernukkitx.event.block;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.property.enums.BigDripleafTilt;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;


public class BigDripleafTiltChangeEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private BigDripleafTilt oldTilt;
    private BigDripleafTilt newTilt;

    public BigDripleafTiltChangeEvent(Block block, BigDripleafTilt oldTilt, BigDripleafTilt newTilt) {
        super(block);
        this.oldTilt = oldTilt;
        this.newTilt = newTilt;
    }

    public BigDripleafTilt getOldTilt() {
        return oldTilt;
    }

    public BigDripleafTilt getNewTilt() {
        return newTilt;
    }

    public void setNewTilt(BigDripleafTilt newTilt) {
        this.newTilt = newTilt;
    }
}
