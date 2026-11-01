package org.powernukkitx.entity.effect;

import java.awt.*;

public class EffectSpeed extends Effect {

    public EffectSpeed() {
        super(EffectType.SPEED, "%potion.moveSpeed", new Color(51, 235, 255));
    }

    /**
     * How much this effect scales the movement speed of whoever carries it. A level below 1, from
     * a negative amplifier, counts as level 1.
     *
     * @param level the level of the effect
     * @return the factor to apply to the movement speed, never below 1.2
     */
    public static float getSpeedFactor(int level) {
        return 1 + 0.2f * Math.max(1, level);
    }
}
