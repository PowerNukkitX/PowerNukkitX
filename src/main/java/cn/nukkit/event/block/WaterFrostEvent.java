package cn.nukkit.event.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

@PowerNukkitOnly
public class WaterFrostEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @PowerNukkitOnly
    public static HandlerList getHandlers() {
        return handlers;
    }

    @PowerNukkitOnly
    protected final Entity entity;

    @PowerNukkitOnly
    public WaterFrostEvent(Block block, Entity entity) {
        super(block);
        this.entity = entity;
    }

    @PowerNukkitOnly
    public Entity getEntity() {
        return entity;
    }
}
