package org.powernukkitx.event.entity;

import org.powernukkitx.entity.Entity;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EntityCombustByEntityEvent extends EntityCombustEvent {
    protected final Entity combuster;

    public EntityCombustByEntityEvent(Entity combuster, Entity combustee, int duration) {
        super(combustee, duration);
        this.combuster = combuster;
    }

    public Entity getCombuster() {
        return combuster;
    }
}
