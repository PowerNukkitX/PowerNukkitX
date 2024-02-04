package cn.nukkit.entity.effect;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;

import java.awt.*;

public class EffectSpeed extends Effect {

    public EffectSpeed() {
        super(EffectType.SPEED, "%potion.moveSpeed", new Color(51, 235, 255));
    }

    @Override
    public void add(Entity entity) {
        if (entity instanceof EntityLiving living) {

            Effect oldEffect = living.getEffect(this.getType());
            if (oldEffect != null) {
                living.setMovementSpeed(living.getMovementSpeed() / (1 + 0.2f * oldEffect.getLevel()));
            }

            living.setMovementSpeed(living.getMovementSpeed() * (1 + 0.2f * this.getLevel()));
        }
    }

    @Override
    public void remove(Entity entity) {
        if (entity instanceof EntityLiving living) {
            living.setMovementSpeed(living.getMovementSpeed() / (1 + 0.2f * this.getLevel()));
        }
    }
}
