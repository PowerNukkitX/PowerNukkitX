package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockFormEvent extends BlockGrowEvent implements Cancellable {

    public BlockFormEvent(Block block, Block newState) {
        super(block, newState);
    }
}
