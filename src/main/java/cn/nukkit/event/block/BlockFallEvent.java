package cn.nukkit.event.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public class BlockFallEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public BlockFallEvent(Block block) {
        super(block);
    }
}
