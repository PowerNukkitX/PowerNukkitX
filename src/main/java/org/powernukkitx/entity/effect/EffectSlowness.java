package org.powernukkitx.entity.effect;

import java.awt.*;

public class EffectSlowness extends Effect {

    public EffectSlowness() {
        super(EffectType.SLOWNESS, "%potion.moveSlowdown", new Color(139, 175, 224), true);
    }

    /**
     * How much this effect scales the movement speed of whoever carries it. A high enough level
     * brings them to a standstill instead of turning their speed negative, and a level below 1, from
     * a negative amplifier, counts as level 1.
     *
     * @param level the level of the effect
     * @return the factor to apply to the movement speed, between 0 and 0.85
     */
    public static float getSpeedFactor(int level) {
        return Math.max(1 - 0.15f * Math.max(1, level), 0f);
    }
}
