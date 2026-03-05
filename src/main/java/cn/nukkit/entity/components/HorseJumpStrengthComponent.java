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
        if (value != null && !Float.isFinite(value)) value = null;
        if (rangeMin != null && !Float.isFinite(rangeMin)) rangeMin = null;
        if (rangeMax != null && !Float.isFinite(rangeMax)) rangeMax = null;

        if (value != null) {
            value = Math.max(0.0f, value);
            rangeMin = null;
            rangeMax = null;
        } else {
            float mn = (rangeMin == null) ? 0.0f : Math.max(0.0f, rangeMin);
            float mx = (rangeMax == null) ? mn   : Math.max(mn, rangeMax);
            rangeMin = mn;
            rangeMax = mx;
        }
    }

    public boolean isFixed() {
        return value != null;
    }

    public float resolveSpawnValue(RandomGenerator rnd) {
        if (value != null) return value;

        float mn = (rangeMin == null) ? 0.0f : rangeMin;
        float mx = (rangeMax == null) ? mn   : rangeMax;

        if (mx <= mn) return mn;

        return mn + (rnd.nextFloat() * (mx - mn));
    }

    public static HorseJumpStrengthComponent defaults() {
        return new HorseJumpStrengthComponent(null, null, null);
    }

    public static HorseJumpStrengthComponent fixed(float v) {
        return new HorseJumpStrengthComponent(v, null, null);
    }

    public static HorseJumpStrengthComponent range(float min, float max) {
        return new HorseJumpStrengthComponent(null, min, max);
    }

    public static HorseJumpStrengthComponent fromVanillaRange(float min, float max) {
        return range(min, max);
    }
}
