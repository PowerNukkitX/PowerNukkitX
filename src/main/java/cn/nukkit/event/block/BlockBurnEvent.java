package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockBurnEvent extends BlockEvent implements Cancellable {

    public BlockBurnEvent(Block block) {
        super(block);
    }
}
