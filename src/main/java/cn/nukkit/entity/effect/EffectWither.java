package cn.nukkit.entity.effect;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;

import java.awt.*;

public class EffectWither extends Effect {
    /**
     * @deprecated 
     */
    

    public EffectWither() {
        super(EffectType.WITHER, "%potion.wither", new Color(115, 97, 86), true);
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
        entity.attack(new EntityDamageEvent(entity, EntityDamageEvent.DamageCause.MAGIC, 1));
    }
}
