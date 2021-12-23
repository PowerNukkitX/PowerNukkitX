package cn.nukkit.event.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.Block;
import cn.nukkit.event.HandlerList;

@PowerNukkitOnly
public class ConduitActivateEvent extends BlockEvent {

    private static final HandlerList handlers = new HandlerList();

    @PowerNukkitOnly
    public ConduitActivateEvent(Block block) {
        super(block);
    }

    @PowerNukkitOnly
    public static HandlerList getHandlers() {
        return handlers;
    }

}
