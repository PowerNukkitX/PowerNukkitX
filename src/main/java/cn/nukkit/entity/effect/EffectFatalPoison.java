package cn.nukkit.entity.effect;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;

import java.awt.*;

public class EffectFatalPoison extends Effect {

    public EffectFatalPoison() {
        super(EffectType.FATAL_POISON, "%potion.poison", new Color(78, 147, 49), true);
    }

    @Override
    public int getInterval() {
        return 25 >> this.getAmplifier();
    }

    @Override
    public void apply(Entity entity, double tickCount) {
        entity.attack(new EntityDamageEvent(entity, EntityDamageEvent.DamageCause.MAGIC, 1));
    }
}
