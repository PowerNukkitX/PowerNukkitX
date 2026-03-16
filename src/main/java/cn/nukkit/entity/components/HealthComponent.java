package cn.nukkit.entity.components;

import cn.nukkit.entity.Entity;

import java.util.random.RandomGenerator;

/**
 * Bedrock component: {@code minecraft:health}.
 *
 * Defines the base maximum health of an entity.
 *
 * <p>The health value may be configured either as a fixed value or as a
 * range. When a range is provided, a random value is generated at spawn
 * time to simulate natural variation between entities.</p>
 *
 * <p><b>Properties:</b></p>
 * <ul>
 *   <li>{@code value} — Fixed maximum health value.</li>
 *   <li>{@code range_min} — Minimum health value used for spawn variation.</li>
 *   <li>{@code range_max} — Maximum health value used for spawn variation.</li>
 * </ul>
 *
 * <p>If {@code value} is defined, the range values are ignored.</p>
 *
 * <p>If a range is defined, the effective health value is resolved at
 * spawn time using:</p>
 *
 * <pre>{@code
 * health = random(rangeMin, rangeMax)
 * }</pre>
 * 
 * @author Curse
 */
public record HealthComponent(Integer value, Integer rangeMin, Integer rangeMax) {
    public HealthComponent {
        Integer v  = value;
        Integer mn = rangeMin;
        Integer mx = rangeMax;

        // sanitize fixed value
        if (v != null) {
            if (v < 1) v = 1;
            mn = null;
            mx = null;
        } else {
            // sanitize range
            if (mn != null && mn < 1) mn = 1;
            if (mx != null && mx < 1) mx = 1;

            // exactly one side set → fixed
            if (mn != null && mx == null) {
                v  = mn;
                mn = null;
                mx = null;
            } else if (mx != null && mn == null) {
                v  = mx;
                mn = null;
                mx = null;
            } else if (mn != null && mx != null) {
                int a = Math.min(mn, mx);
                int b = Math.max(mn, mx);

                // equal → fixed
                if (a == b) {
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

    public int resolveSpawnValue(RandomGenerator rnd) {
        if (value != null) return value;
        int mn = (rangeMin == null) ? 1 : Math.max(1, rangeMin);
        int mx = (rangeMax == null) ? mn : Math.max(mn, rangeMax);
        if (mx <= mn) return mn;
        return mn + rnd.nextInt((mx - mn) + 1);
    }

    public static HealthComponent defaults() {
        return value(Entity.DEFAULT_HEALTH);
    }

    public static HealthComponent value(int value) {
        return new HealthComponent(value, null, null);
    }

    public static HealthComponent range(int min, int max) {
        return new HealthComponent(null, min, max);
    }
}
