package cn.nukkit.event.entity;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.potion.Effect;

@PowerNukkitXOnly
public class EntityEffectUpdateEvent extends EntityEvent implements Cancellable {

    private Effect oldEffect;
    private Effect newEffect;

    public EntityEffectUpdateEvent(Entity entity, Effect oldEffect, Effect newEffect) {
        this.entity = entity;
        this.oldEffect = oldEffect;
        this.newEffect = newEffect;
    }

    public Effect getOldEffect() {
        return this.oldEffect;
    }

    public Effect getNewEffect() {
        return this.newEffect;
    }
}
