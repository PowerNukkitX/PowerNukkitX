package cn.nukkit.event.entity;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

@PowerNukkitXOnly
@Since("1.19.50-r4")
public class FarmLandDecayEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Block farm;

    public FarmLandDecayEvent(Entity entity, Block farm) {
        this.entity = entity;
        this.farm = farm;
    }

    public Block getBlock() {
        return farm;
    }
}
