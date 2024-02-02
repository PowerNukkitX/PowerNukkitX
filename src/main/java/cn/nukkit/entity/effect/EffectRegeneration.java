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
        int interval;
        if ((interval = (40 >> this.getAmplifier())) > 0) {
            return (this.getDuration() % interval) == 0;
        }
        return true;
    }

    @Override
    public void apply(Entity entity, double health) {
        if (entity.getHealth() < entity.getMaxHealth()) {
            entity.heal(new EntityRegainHealthEvent(entity, 1, EntityRegainHealthEvent.CAUSE_MAGIC));
        }
    }
}
