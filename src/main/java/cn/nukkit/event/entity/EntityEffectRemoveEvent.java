package cn.nukkit.event.entity;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.potion.Effect;

@PowerNukkitXOnly
public class EntityEffectRemoveEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private Effect removeEffect;

    public EntityEffectRemoveEvent(Entity entity, Effect effect) {
        this.entity = entity;
        this.removeEffect = effect;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Effect getRemoveEffect() {
        return removeEffect;
    }
}
