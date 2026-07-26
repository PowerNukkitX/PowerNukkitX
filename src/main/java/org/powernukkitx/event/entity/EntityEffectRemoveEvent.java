package org.powernukkitx.event.entity;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.event.Cancellable;
import org.powernukkitx.event.HandlerList;


public class EntityEffectRemoveEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Effect removeEffect;

    public EntityEffectRemoveEvent(Entity entity, Effect effect) {
        this.entity = entity;
        this.removeEffect = effect;
    }

    public Effect getRemoveEffect() {
        return removeEffect;
    }

}
