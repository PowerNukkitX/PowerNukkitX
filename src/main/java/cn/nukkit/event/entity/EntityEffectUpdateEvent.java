package cn.nukkit.event.entity;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.potion.Effect;

@PowerNukkitXOnly
public class EntityEffectUpdateEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private Effect updateEffect;

    public EntityEffectUpdateEvent(Entity entity, Effect effect) {
        this.entity = entity;
        this.updateEffect = effect;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Effect getUpdateEffect() {
        return updateEffect;
    }
}
