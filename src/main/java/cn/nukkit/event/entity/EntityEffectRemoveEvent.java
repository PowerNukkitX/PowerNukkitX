package cn.nukkit.event.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.entity.effect.Effect;


public class EntityEffectRemoveEvent extends EntityEvent implements Cancellable {
    private static final HandlerList $1 = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Effect removeEffect;
    /**
     * @deprecated 
     */
    

    public EntityEffectRemoveEvent(Entity entity, Effect effect) {
        this.entity = entity;
        this.removeEffect = effect;
    }

    public Effect getRemoveEffect() {
        return removeEffect;
    }

}
