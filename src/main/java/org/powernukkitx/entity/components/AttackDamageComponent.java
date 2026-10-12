package org.powernukkitx.entity.components;

import com.google.common.base.Preconditions;

import java.util.random.RandomGenerator;

/**
 * Bedrock component: {@code minecraft:attack_damage}.
 *
 * Represents the generic actor-attribute definition consumed after
 * {@code minecraft:attack} during mob component initialization.
 *
 * @author Curse
 */
public record AttackDamageComponent(Float min, Float max, float valueMin, float valueMax) {

    public AttackDamageComponent {
        Preconditions.checkArgument(min == null || Float.isFinite(min), "min must be finite");
        Preconditions.checkArgument(max == null || Float.isFinite(max), "max must be finite");
        Preconditions.checkArgument(Float.isFinite(valueMin), "valueMin must be finite");
        Preconditions.checkArgument(Float.isFinite(valueMax), "valueMax must be finite");
        Preconditions.checkArgument(valueMax >= valueMin, "valueMax must be higher or equal to valueMin");
    }

    /**
     * Resolves the configured value range.
     */
    public float resolve(RandomGenerator random) {
        if (Float.compare(valueMin, valueMax) == 0) return valueMin;
        return valueMin + random.nextFloat() * (valueMax - valueMin);
    }

    public static AttackDamageComponent value(float value) {
        return new AttackDamageComponent(null, null, value, value);
    }

    public static AttackDamageComponent range(float min, float max) {
        return new AttackDamageComponent(null, null, min, max);
    }

    public static AttackDamageComponent bounded(float min, float max, float value) {
        return new AttackDamageComponent(min, max, value, value);
    }

    public static AttackDamageComponent boundedRange(float min, float max, float valueMin, float valueMax) {
        return new AttackDamageComponent(min, max, valueMin, valueMax);
    }
}
