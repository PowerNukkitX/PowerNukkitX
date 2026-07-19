package org.powernukkitx.event.entity;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.Event;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class EntityEvent extends Event {

    protected Entity entity;

    public Entity getEntity() {
        return entity;
    }
}
