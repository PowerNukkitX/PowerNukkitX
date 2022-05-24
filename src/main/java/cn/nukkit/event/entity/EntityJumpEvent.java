package cn.nukkit.event.entity;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class EntityJumpEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public EntityJumpEvent(Entity entity) {
        this.entity = entity;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    private final Entity entity;

    @Override
    public Entity getEntity() {
        return entity;
    }
}
