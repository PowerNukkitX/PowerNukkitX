package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockUpdateEvent extends BlockEvent implements Cancellable {

    public BlockUpdateEvent(Block block) {
        super(block);
    }
}
