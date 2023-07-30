package cn.nukkit.event.blockstate;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockstate.BlockStateRepair;
import cn.nukkit.event.Event;

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockStateRepairEvent extends Event {

    private final BlockStateRepair repair;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockStateRepairEvent(BlockStateRepair repair) {
        this.repair = repair;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockStateRepair getRepair() {
        return repair;
    }
}
