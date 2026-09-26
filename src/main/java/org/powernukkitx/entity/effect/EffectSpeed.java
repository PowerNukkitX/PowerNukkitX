package org.powernukkitx.entity.effect;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityLiving;

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
                restoreSpeed(living, oldEffect.getLevel());
            }

            living.setMovementSpeed(living.getMovementSpeed() * getSpeedFactor(this.getLevel()));
        }
    }

    @Override
    public void remove(Entity entity) {
        if (entity instanceof EntityLiving living) {
            restoreSpeed(living, this.getLevel());
        }
    }

    private static void restoreSpeed(EntityLiving living, int level) {
        living.setMovementSpeed(living.getMovementSpeed() / getSpeedFactor(level));
    }

    /**
     * How much this effect scales the movement speed of whoever carries it.
     *
     * @param level the level of the effect
     * @return the factor to apply to the movement speed
     */
    public static float getSpeedFactor(int level) {
        return 1 + 0.2f * level;
    }
}
