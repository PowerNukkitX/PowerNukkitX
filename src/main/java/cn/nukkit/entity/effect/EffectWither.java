package cn.nukkit.entity.effect;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;

import java.awt.*;

public class EffectWither extends Effect {

    public EffectWither() {
        super(EffectType.WITHER, "%potion.wither", new Color(115, 97, 86), true);
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
