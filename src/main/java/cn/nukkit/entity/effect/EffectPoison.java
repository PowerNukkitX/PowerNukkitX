package cn.nukkit.entity.effect;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;

import java.awt.*;

public class EffectPoison extends Effect {

    public EffectPoison() {
        super(EffectType.POISON, "%potion.poison", new Color(135, 163, 99), true);
    }

    @Override
    public int getInterval() {
        return 25 >> this.getAmplifier();
    }

    @Override
    public void apply(Entity entity, double tickCount) {
        if (entity.getHealthCurrent() > 1) {
            entity.attack(new EntityDamageEvent(entity, EntityDamageEvent.DamageCause.MAGIC, 1));
        }
    }
}
