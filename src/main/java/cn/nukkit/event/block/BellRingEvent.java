package cn.nukkit.event.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.BlockBell;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

@PowerNukkitOnly
public class BellRingEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @PowerNukkitOnly
    public static HandlerList getHandlers() {
        return handlers;
    }

    private final RingCause cause;
    private final Entity entity;

    @PowerNukkitOnly
    public BellRingEvent(BlockBell bell, RingCause cause, Entity entity) {
        super(bell);
        this.cause = cause;
        this.entity = entity;
    }

    @Override
    public BlockBell getBlock() {
        return (BlockBell) super.getBlock();
    }

    @PowerNukkitOnly
    public Entity getEntity() {
        return entity;
    }

    @PowerNukkitOnly
    public RingCause getCause() {
        return cause;
    }

    @PowerNukkitOnly
    public enum RingCause {
        @PowerNukkitOnly HUMAN_INTERACTION,
        @PowerNukkitOnly REDSTONE,
        @PowerNukkitOnly PROJECTILE,
        @PowerNukkitOnly DROPPED_ITEM,
        @PowerNukkitOnly UNKNOWN
    }

}
