package cn.nukkit.entity.effect;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;

import java.awt.*;

public class EffectPoison extends Effect {

    public EffectPoison() {
        super(EffectType.POISON, "%potion.poison", new Color(135, 163, 99), true);
    }

    @Override
    public boolean canTick() {
        int interval = 25 >> this.getAmplifier();
        return interval > 0 && this.getDuration() % interval == 0;
    }

    @Override
    public boolean canTick(Entity entity) {
        int interval = 25 >> this.getAmplifier();
        if (interval > 0) {
            if (this.isInfinite()) {
                return entity.ticksLived % interval == 0;
            }
            return this.getDuration() % interval == 0;
        }
        return false;
    }

    @Override
    public void apply(Entity entity, double tickCount) {
        if (this.canTick(entity)) {
            if (entity.getHealthCurrent() > 1) {
                entity.attack(new EntityDamageEvent(entity, EntityDamageEvent.DamageCause.MAGIC, 1));
            }
        }
    }
}
