package cn.nukkit.event.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockFallEvent extends BlockEvent implements Cancellable {

    public BlockFallEvent(Block block) {
        super(block);
    }
}
