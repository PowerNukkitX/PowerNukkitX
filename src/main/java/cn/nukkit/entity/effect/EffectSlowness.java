package cn.nukkit.entity.effect;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;

import java.awt.*;

public class EffectSlowness extends Effect {

    public EffectSlowness() {
        super(EffectType.SLOWNESS, "%potion.moveSlowdown", new Color(139, 175, 224), true);
    }

    @Override
    public void add(Entity entity) {
        if (entity instanceof EntityLiving living) {

            Effect oldEffect = living.getEffect(this.getType());
            if (oldEffect != null) {
                living.setMovementSpeed(living.getMovementSpeed() / (1 - 0.15f * oldEffect.getLevel()));
            }

            living.setMovementSpeed(living.getMovementSpeed() * (1 - 0.15f * this.getLevel()));
        }
    }

    @Override
    public void remove(Entity entity) {
        if (entity instanceof EntityLiving living) {
            living.setMovementSpeed(living.getMovementSpeed() / (1 - 0.15f * this.getLevel()));
        }
    }
}
