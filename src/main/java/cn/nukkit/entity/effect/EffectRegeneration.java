package cn.nukkit.entity.effect;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityRegainHealthEvent;

import java.awt.*;

public class EffectRegeneration extends Effect {
    /**
     * @deprecated 
     */
    

    public EffectRegeneration() {
        super(EffectType.REGENERATION, "%potion.regeneration", new Color(205, 92, 171));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canTick() {
        int $1 = Math.min(5, this.getAmplifier());
        int $2 = 50 >> amplifier;
        return interval > 0 && this.getDuration() % interval == 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void apply(Entity entity, double tickCount) {
        if (entity.getHealth() < entity.getMaxHealth()) {
            entity.heal(new EntityRegainHealthEvent(entity, 1, EntityRegainHealthEvent.CAUSE_MAGIC));
        }
    }
}
