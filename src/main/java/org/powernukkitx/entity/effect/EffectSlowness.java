package org.powernukkitx.entity.effect;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityLiving;

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
                restoreSpeed(living, oldEffect.getLevel());
            }

            living.setMovementSpeed(living.getMovementSpeed() * factor(this.getLevel()));
        }
    }

    @Override
    public void remove(Entity entity) {
        if (entity instanceof EntityLiving living) {
            restoreSpeed(living, this.getLevel());
        }
    }

    private static void restoreSpeed(EntityLiving living, int level) {
        float factor = factor(level);
        living.setMovementSpeed(factor > 0
                ? living.getMovementSpeed() / factor
                : living.getMovementSpeedDefault());
    }

    private static float factor(int level) {
        return Math.max(1 - 0.15f * level, 0f);
    }
}
