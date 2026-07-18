package org.powernukkitx.event.block;

import org.powernukkitx.block.Block;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;

import javax.annotation.Nullable;


public class WaterFrostEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected final Entity entity;

    public WaterFrostEvent(Block block, @Nullable Entity entity) {
        super(block);
        this.entity = entity;
    }

    public @Nullable Entity getEntity() {
        return entity;
    }
}
