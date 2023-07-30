package cn.nukkit.event.block;

import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class LeavesDecayEvent extends BlockEvent implements Cancellable {

    public LeavesDecayEvent(Block block) {
        super(block);
    }
}
