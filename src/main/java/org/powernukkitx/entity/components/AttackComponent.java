package org.powernukkitx.entity.components;

import com.google.common.base.Preconditions;

import java.util.random.RandomGenerator;

/**
 * Bedrock component: {@code minecraft:attack}.
 *
 * Defines the damage range used to update the {@code minecraft:attack_damage}
 * attribute when an attack is performed.
 *
 * @author Curse
 */
public record AttackComponent(float min, float max) {

    public AttackComponent {
        Preconditions.checkArgument(Float.isFinite(min), "min must be finite");
        Preconditions.checkArgument(Float.isFinite(max), "max must be finite");
        Preconditions.checkArgument(max >= min, "max value must be higher or equal to min value");
    }

    /**
     * Resolves the damage value for one attack.
     */
    public float resolve(RandomGenerator random) {
        if (Float.compare(min, max) == 0) return min;
        return min + random.nextFloat() * (max - min);
    }

    public static AttackComponent value(float value) {
        return new AttackComponent(value, value);
    }

    public static AttackComponent range(float min, float max) {
        return new AttackComponent(min, max);
    }
}
