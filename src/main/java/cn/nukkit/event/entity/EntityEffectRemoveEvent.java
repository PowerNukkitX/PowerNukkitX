package cn.nukkit.event.entity;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.potion.Effect;

@PowerNukkitXOnly
public class EntityEffectRemoveEvent extends EntityEvent implements Cancellable {

    private Effect removeEffect;

    public EntityEffectRemoveEvent(Entity entity, Effect effect) {
        this.entity = entity;
        this.removeEffect = effect;
    }

    public Effect getRemoveEffect() {
        return removeEffect;
    }
}
