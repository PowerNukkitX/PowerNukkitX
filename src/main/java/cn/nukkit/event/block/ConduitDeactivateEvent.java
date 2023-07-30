package cn.nukkit.event.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.Block;

@PowerNukkitOnly
public class ConduitDeactivateEvent extends BlockEvent {

    @PowerNukkitOnly
    public ConduitDeactivateEvent(Block block) {
        super(block);
    }
}
