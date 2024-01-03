package cn.nukkit.event.blockstate;

import cn.nukkit.blockstate.BlockStateRepair;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;

/**
 * @author joserobjr
 */
public class BlockStateRepairEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }
    
    private final BlockStateRepair repair;

    public BlockStateRepairEvent(BlockStateRepair repair) {
        this.repair = repair;
    }

    public BlockStateRepair getRepair() {
        return repair;
    }
}
