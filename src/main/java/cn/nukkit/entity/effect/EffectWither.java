package cn.nukkit.entity.effect;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;

import java.awt.*;

public class EffectWither extends Effect {

    public EffectWither() {
        super(EffectType.WITHER, "%potion.wither", new Color(115, 97, 86), true);
    }

    @Override
    public boolean canTick() {
        int interval;
        if ((interval = (50 >> this.getAmplifier())) > 0) {
            return (this.getDuration() % interval) == 0;
        }
        return true;
    }

    @Override
    public void apply(Entity entity, double health) {
        entity.attack(new EntityDamageEvent(entity, EntityDamageEvent.DamageCause.MAGIC, 1));
    }
}
