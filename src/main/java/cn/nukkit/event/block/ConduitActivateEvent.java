package cn.nukkit.event.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.Block;

@PowerNukkitOnly
public class ConduitActivateEvent extends BlockEvent {

    @PowerNukkitOnly
    public ConduitActivateEvent(Block block) {
        super(block);
    }
}
