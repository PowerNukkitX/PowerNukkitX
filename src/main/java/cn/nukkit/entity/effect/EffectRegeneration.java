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
    public void apply(Entity entity, double tickCount) {
        if (entity.getHealth() < entity.getMaxHealth()) {
            entity.heal(new EntityRegainHealthEvent(entity, 1, EntityRegainHealthEvent.CAUSE_MAGIC));
        }
    }
}
