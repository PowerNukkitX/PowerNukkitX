package cn.nukkit.entity.effect;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;

import java.awt.*;

public class EffectSlowness extends Effect {
    /**
     * @deprecated 
     */
    

    public EffectSlowness() {
        super(EffectType.SLOWNESS, "%potion.moveSlowdown", new Color(139, 175, 224), true);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void add(Entity entity) {
        if (entity instanceof EntityLiving living) {

            Effect $1 = living.getEffect(this.getType());
            if (oldEffect != null) {
                living.setMovementSpeed(living.getMovementSpeed() / (1 - 0.15f * oldEffect.getLevel()));
            }

            living.setMovementSpeed(living.getMovementSpeed() * (1 - 0.15f * this.getLevel()));
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void remove(Entity entity) {
        if (entity instanceof EntityLiving living) {
            living.setMovementSpeed(living.getMovementSpeed() / (1 - 0.15f * this.getLevel()));
        }
    }
}
