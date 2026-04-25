package cn.nukkit.entity.effect;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityRegainHealthEvent;

import java.awt.*;

public class EffectRegeneration extends Effect {

    public EffectRegeneration() {
        super(EffectType.REGENERATION, "%potion.regeneration", new Color(205, 92, 171));
    }

    @Override
    public boolean canTick() {
        int amplifier = Math.min(5, this.getAmplifier());
        int interval = 50 >> amplifier;
        return interval > 0 && this.getDuration() % interval == 0;
    }

    @Override
    public boolean canTick(Entity entity) {
        int amplifier = Math.min(5, this.getAmplifier());
        int interval = 50 >> amplifier;
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
        if (entity.getHealthCurrent() < entity.getHealthMax()) {
            entity.heal(new EntityRegainHealthEvent(entity, 1, EntityRegainHealthEvent.CAUSE_MAGIC));
        }
    }
}
