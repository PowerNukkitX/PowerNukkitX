package cn.nukkit.event.player;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.entity.EntityEvent;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class EntityFreezeEvent extends EntityEvent implements Cancellable {

    private final Entity entity;

    public EntityFreezeEvent(Entity human) {
        this.entity = human;
    }
}
