package cn.nukkit.entity.components;

import cn.nukkit.entity.Entity;

import java.util.random.RandomGenerator;

/**
 * Bedrock component: {@code minecraft:movement}.
 *
 * Defines the base movement speed of an entity.
 *
 * <p>The movement value may be configured either as a fixed value or as a
 * range. When a range is provided, a random value is generated at spawn
 * time to simulate natural variation between entities.</p>
 *
 * <p><b>Properties:</b></p>
 * <ul>
 *   <li>{@code value} — Fixed movement speed value.</li>
 *   <li>{@code range_min} — Minimum movement value used for spawn variation.</li>
 *   <li>{@code range_max} — Maximum movement value used for spawn variation.</li>
 * </ul>
 *
 * <p>If {@code value} is defined, the range values are ignored.</p>
 *
 * <p>If a range is defined, the effective movement value is resolved at
 * spawn time using:</p>
 *
 * <pre>{@code
 * movement = random(rangeMin, rangeMax)
 * }</pre>
 *
 * <p>This component normalizes invalid values and provides helper methods
 * to resolve the effective movement used during entity initialization.</p>
 */
public record MovementComponent(Float value, Float rangeMin, Float rangeMax) {
    public MovementComponent {
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

    /** True only for real ranges (genetics) */
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
        float mn = (rangeMin == null) ? Entity.DEFAULT_SPEED : Math.max(0f, rangeMin);
        float mx = (rangeMax == null) ? mn : Math.max(mn, rangeMax);
        if (mx <= mn) return mn;
        return mn + (rnd.nextFloat() * (mx - mn));
    }

    public static MovementComponent defaults() {
        return value(Entity.DEFAULT_SPEED);
    }

    public static MovementComponent value(float value) {
        return new MovementComponent(value, null, null);
    }

    public static MovementComponent range(float min, float max) {
        return new MovementComponent(null, min, max);
    }
}