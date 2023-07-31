package cn.nukkit.event.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.impl.BlockBigDripleaf;
import cn.nukkit.event.Cancellable;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BigDripleafTiltChangeEvent extends BlockEvent implements Cancellable {

    private BlockBigDripleaf.Tilt oldTilt;
    private BlockBigDripleaf.Tilt newTilt;

    public BigDripleafTiltChangeEvent(Block block, BlockBigDripleaf.Tilt oldTilt, BlockBigDripleaf.Tilt newTilt) {
        super(block);
        this.oldTilt = oldTilt;
        this.newTilt = newTilt;
    }

    public BlockBigDripleaf.Tilt getOldTilt() {
        return oldTilt;
    }

    public BlockBigDripleaf.Tilt getNewTilt() {
        return newTilt;
    }

    public void setNewTilt(BlockBigDripleaf.Tilt newTilt) {
        this.newTilt = newTilt;
    }
}
