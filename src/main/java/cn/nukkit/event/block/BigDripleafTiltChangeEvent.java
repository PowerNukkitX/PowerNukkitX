package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBigDripleaf;
import cn.nukkit.block.property.enums.BigDripleafTilt;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import lombok.Getter;


public class BigDripleafTiltChangeEvent extends BlockEvent implements Cancellable {

    @Getter
    private static final HandlerList handlers = new HandlerList();

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

    public void setOldTilt(BigDripleafTilt oldTilt) {
        this.oldTilt = oldTilt;
    }

    public void setNewTilt(BigDripleafTilt newTilt) {
        this.newTilt = newTilt;
    }
}
