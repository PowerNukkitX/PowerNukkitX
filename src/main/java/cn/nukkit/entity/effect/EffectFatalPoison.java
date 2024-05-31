package cn.nukkit.entity.effect;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;

import java.awt.*;

public class EffectFatalPoison extends Effect {
    /**
     * @deprecated 
     */
    

    public EffectFatalPoison() {
        super(EffectType.FATAL_POISON, "%potion.poison", new Color(78, 147, 49), true);
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
            entity.attack(new EntityDamageEvent(entity, EntityDamageEvent.DamageCause.MAGIC, 1));
        }
    }
}
