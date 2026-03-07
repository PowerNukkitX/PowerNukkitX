package cn.nukkit.entity.components;

import java.util.random.RandomGenerator;


/**
 * Bedrock component: {@code minecraft:horse_jump_strength}.
 *
 * Defines the jump strength attribute used by horse-like entities
 * (for example horses, donkeys, and mules). This value determines
 * how high the entity can jump when ridden.
 *
 * <p>The jump strength may be configured as either a fixed value or
 * as a range. When a range is provided, a random value is generated
 * at spawn time to simulate natural variation between entities.</p>
 *
 * <p><b>Properties:</b></p>
 * <ul>
 *   <li>{@code value} — Fixed jump strength value.</li>
 *   <li>{@code range_min} — Minimum jump strength used for spawn variation.</li>
 *   <li>{@code range_max} — Maximum jump strength used for spawn variation.</li>
 * </ul>
 *
 * <p>If {@code value} is defined, the range values are ignored.</p>
 *
 * <p>If a range is defined, the effective jump strength is resolved
 * at spawn time using:</p>
 *
 * <pre>{@code
 * jumpStrength = random(rangeMin, rangeMax)
 * }</pre>
 * 
 * @author Curse
 */
public record HorseJumpStrengthComponent(Float value, Float rangeMin, Float rangeMax) {
    public HorseJumpStrengthComponent {
        Float v  = value;
        Float mn = rangeMin;
        Float mx = rangeMax;

        // sanitize fixed value
        if (v != null) {
            if (!Float.isFinite(v) || v < 0f) v = 0f;
            mn = null;
            mx = null;
        } else {
            // sanitize range
            if (mn != null && (!Float.isFinite(mn) || mn < 0f)) mn = 0f;
            if (mx != null && (!Float.isFinite(mx) || mx < 0f)) mx = 0f;

            // exactly one side set -> fixed
            if (mn != null && mx == null) {
                v  = mn;
                mn = null;
                mx = null;
            } else if (mx != null && mn == null) {
                v  = mx;
                mn = null;
                mx = null;
            } else if (mn != null && mx != null) {
                float a = Math.min(mn, mx);
                float b = Math.max(mn, mx);

                // equal -> fixed
                if (Float.compare(a, b) == 0) {
                    v  = a;
                    mn = null;
                    mx = null;
                } else {
                    // real range
                    v  = null;
                    mn = a;
                    mx = b;
                }
            } else {
                // neither set
                v  = null;
                mn = null;
                mx = null;
            }
        }

        value    = v;
        rangeMin = mn;
        rangeMax = mx;
    }

    public boolean hasRange() {
        return value == null
                && rangeMin != null
                && rangeMax != null
                && rangeMin < rangeMax;
    }

    public boolean isFixed() {
        return value != null;
    }

    public float resolveSpawnValue(RandomGenerator rnd) {
        if (value != null) return value;
        float mn = (rangeMin == null) ? 0.0f : Math.max(0f, rangeMin);
        float mx = (rangeMax == null) ? mn   : Math.max(mn, rangeMax);
        if (mx <= mn) return mn;
        return mn + (rnd.nextFloat() * (mx - mn));
    }

    public static HorseJumpStrengthComponent defaults() {
        return new HorseJumpStrengthComponent(null, null, null);
    }

    public static HorseJumpStrengthComponent value(float value) {
        return new HorseJumpStrengthComponent(value, null, null);
    }

    public static HorseJumpStrengthComponent range(float min, float max) {
        return new HorseJumpStrengthComponent(null, min, max);
    }
}
