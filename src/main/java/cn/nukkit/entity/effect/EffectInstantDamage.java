package cn.nukkit.entity.effect;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;

import java.awt.*;

public class EffectInstantDamage extends InstantEffect {

    public EffectInstantDamage() {
        super(EffectType.INSTANT_DAMAGE, "%potion.harm", new Color(169, 101, 106), true);
    }

    @Override
    public void apply(Entity entity, double tickCount) {
        double amount = (6 << this.getAmplifier()) * tickCount;
        if (entity.isUndead()) {
            entity.heal(new EntityRegainHealthEvent(entity, (float) amount, EntityRegainHealthEvent.CAUSE_MAGIC));
        } else {
            entity.attack(new EntityDamageEvent(entity, EntityDamageEvent.DamageCause.MAGIC, (float) amount));
        }
    }
}
