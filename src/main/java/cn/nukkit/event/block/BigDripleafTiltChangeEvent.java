package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.block.property.enums.BigDripleafTilt;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;


public class BigDripleafTiltChangeEvent extends BlockEvent implements Cancellable {

    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private BigDripleafTilt oldTilt;
    private BigDripleafTilt newTilt;
    /**
     * @deprecated 
     */
    

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
    /**
     * @deprecated 
     */
    

    public void setNewTilt(BigDripleafTilt newTilt) {
        this.newTilt = newTilt;
    }
}
