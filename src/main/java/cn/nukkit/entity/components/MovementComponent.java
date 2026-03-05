package cn.nukkit.entity.components;

import java.util.random.RandomGenerator;


/**
 * Bedrock component: {@code minecraft:movement}.
 *
 * Defines the base movement speed of an entity. This value represents the
 * base movement capability.
 *
 * <p>Optionally, a genetic range may be defined. When a range is present,
 * a random value within the range is sampled when the entity spawns,
 * allowing natural variation between entities.</p>
 *
 * <p><b>Properties:</b></p>
 * <ul>
 *   <li>{@code value} — Base movement speed of the entity.</li>
 *   <li>{@code range_min} — Minimum genetic variation factor.</li>
 *   <li>{@code range_max} — Maximum genetic variation factor.</li>
 * </ul>
 *
 * <p>If no range is defined, the entity always uses the base movement
 * speed. If a range is defined, the final movement value used by the
 * entity at spawn time becomes:</p>
 *
 * <pre>{@code
 * finalSpeed = moveSpeed * random(rangeMin, rangeMax)
 * }</pre>
 * 
 * @author Curse
 */
public record MovementComponent(float moveSpeed, Float rangeMin, Float rangeMax) {
    public MovementComponent {
        // Sanitize base values
        moveSpeed   = Float.isFinite(moveSpeed) ? Math.max(0f, moveSpeed) : 0f;

        Float rMin = rangeMin;
        Float rMax = rangeMax;

        if (rMin != null && !Float.isFinite(rMin)) rMin = null;
        if (rMax != null && !Float.isFinite(rMax)) rMax = null;

        // If exactly one is set -> fixed range
        if (rMin != null && rMax == null) rMax = rMin;
        if (rMax != null && rMin == null) rMin = rMax;

        // If neither set -> keep both null (IMPORTANT)
        if (rMin == null && rMax == null) {
            rangeMin = null;
            rangeMax = null;
        } else {
            // Normalize order + clamp
            @SuppressWarnings("null")
            float mn = Math.max(0f, Math.min(rMin, rMax));
            @SuppressWarnings("null")
            float mx = Math.max(0f, Math.max(rMin, rMax));

            rangeMin = mn;
            rangeMax = mx;
        }
    }

    public boolean hasRange() {
        return rangeMin != null && rangeMax != null;
    }

    public boolean isFixedRange() {
        return rangeMin != null && rangeMax != null && Float.compare(rangeMin, rangeMax) == 0;
    }

    /**
     * Resolves the "genetics" factor from the optional range.
     * Returns 1.0f when no range is defined.
     */
    public float resolveRangeFactor(RandomGenerator rnd) {
        if (rangeMin == null || rangeMax == null) return 1.0f;

        float mn = rangeMin;
        float mx = rangeMax;

        if (mx <= mn) return mn;
        return mn + (rnd.nextFloat() * (mx - mn));
    }

    public static MovementComponent defaults() {
        return new MovementComponent(0.1125f, null, null);
    }

    public static MovementComponent fixed(float moveSpeed) {
        return new MovementComponent(moveSpeed, null, null);
    }

    public static MovementComponent withRange(float moveSpeed, float min, float max) {
        return new MovementComponent(moveSpeed, min, max);
    }
}
