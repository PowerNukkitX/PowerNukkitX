package cn.nukkit.entity.effect;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityRegainHealthEvent;

import java.awt.*;

public class EffectRegeneration extends Effect {

    public EffectRegeneration() {
        super(EffectType.REGENERATION, "%potion.regeneration", new Color(205, 92, 171));
    }

    @Override
    public int getInterval() {
        return 50 >> Math.min(5, this.getAmplifier());
    }

    @Override
    public void apply(Entity entity, double tickCount) {
        if (entity.getHealthCurrent() < entity.getHealthMax()) {
            entity.heal(new EntityRegainHealthEvent(entity, 1, EntityRegainHealthEvent.CAUSE_MAGIC));
        }
    }
}
