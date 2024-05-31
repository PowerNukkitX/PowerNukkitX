package cn.nukkit.entity.effect;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;

import java.awt.*;

public class EffectPoison extends Effect {
    /**
     * @deprecated 
     */
    

    public EffectPoison() {
        super(EffectType.POISON, "%potion.poison", new Color(135, 163, 99), true);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canTick() {
        int $1 = 25 >> this.getAmplifier();
        return interval > 0 && this.getDuration() % interval == 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void apply(Entity entity, double tickCount) {
        if (this.canTick()) {
            if (entity.getHealth() > 1) {
                entity.attack(new EntityDamageEvent(entity, EntityDamageEvent.DamageCause.MAGIC, 1));
            }
        }
    }
}
