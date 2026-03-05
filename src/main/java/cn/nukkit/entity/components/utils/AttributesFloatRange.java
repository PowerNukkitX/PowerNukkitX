package cn.nukkit.entity.components.utils;

import java.util.concurrent.ThreadLocalRandom;


/**
 * Represents a numeric attribute value that may be either fixed or defined as a range.
 * <p>
 * This utility is commonly used for entity attributes such as health, movement speed,
 * or jump strength, where a value can be constant or randomly selected between a
 * minimum and maximum bound.
 * </p>
 * <p>
 * When the range is variable ({@code min != max}), the {@link #roll()} method can be
 * used to obtain a random value within the defined bounds.
 * </p>
 * 
 * @author Curse
 */
public record AttributesFloatRange(float min, float max) {

    public AttributesFloatRange {
        if (!Float.isFinite(min) || !Float.isFinite(max)) {
            throw new IllegalArgumentException("Range values must be finite");
        }
        if (min <= 0f || max <= 0f) {
            throw new IllegalArgumentException("Range values must be > 0");
        }
        if (min > max) {
            float t = min;
            min = max;
            max = t;
        }
    }

    public AttributesFloatRange(float value) {
        this(value, value);
    }

    public boolean isRange() {
        return max > min;
    }

    public float roll() {
        if (!isRange()) return min;
        return ThreadLocalRandom.current().nextFloat(min, Math.nextUp(max));
    }

    public static AttributesFloatRange fixed(float v) {
        return new AttributesFloatRange(v, v);
    }
}
