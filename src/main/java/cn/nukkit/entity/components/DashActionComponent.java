package cn.nukkit.entity.components;

import java.util.Locale;


/**
 * Bedrock component: {@code minecraft:dash}.
 *
 * Defines dash mechanics for rideable entities that can perform a burst
 * movement (for example camels). A dash applies horizontal and vertical
 * momentum and is limited by a cooldown period.
 *
 * <p>The direction used for the dash may be based on either the entity
 * orientation or the passenger orientation.</p>
 *
 * <p><b>Properties:</b></p>
 * <ul>
 *   <li>{@code can_dash_underwater} — If true, the entity may perform a dash while underwater (default: false).</li>
 *   <li>{@code cooldown_time} — Cooldown time between dash uses (default: 1.0).</li>
 *   <li>{@code direction} — Direction source for the dash:
 *       <ul>
 *         <li>{@code entity} — Uses the entity's facing direction.</li>
 *         <li>{@code passenger} — Uses the controlling passenger's facing direction.</li>
 *       </ul>
 *   </li>
 *   <li>{@code horizontal_momentum} — Horizontal momentum applied during the dash (default: 1.0).</li>
 *   <li>{@code vertical_momentum} — Vertical momentum applied during the dash (default: 1.0).</li>
 * </ul>
 */
public record DashActionComponent(Boolean canDashUnderwater, Float cooldownTime, Direction direction, Float horizontalMomentum, Float verticalMomentum) {
    public DashActionComponent {
        if (cooldownTime != null && !Float.isFinite(cooldownTime)) cooldownTime = null;
        if (horizontalMomentum != null && !Float.isFinite(horizontalMomentum)) horizontalMomentum = null;
        if (verticalMomentum != null && !Float.isFinite(verticalMomentum)) verticalMomentum = null;
        if (cooldownTime != null) cooldownTime = Math.max(0.0f, cooldownTime);
        if (horizontalMomentum != null) horizontalMomentum = Math.max(0.0f, horizontalMomentum);
        if (verticalMomentum != null) verticalMomentum = Math.max(0.0f, verticalMomentum);
    }

    /** True when NOTHING is defined -> treat as "component not present". */
    public boolean isEmpty() {
        return canDashUnderwater == null
                && cooldownTime == null
                && direction == null
                && horizontalMomentum == null
                && verticalMomentum == null;
    }

    public boolean resolvedCanDashUnderwater() {
        return canDashUnderwater != null ? canDashUnderwater : false;
    }

    public float resolvedCooldownTime() {
        return cooldownTime != null ? cooldownTime : 1.0f;
    }

    public Direction resolvedDirection() {
        return direction != null ? direction : Direction.ENTITY;
    }

    public float resolvedHorizontalMomentum() {
        return horizontalMomentum != null ? horizontalMomentum : 1.0f;
    }

    public float resolvedVerticalMomentum() {
        return verticalMomentum != null ? verticalMomentum : 1.0f;
    }

    public static DashActionComponent defaults() {
        return new DashActionComponent(null, null, null, null, null);
    }

    public enum Direction {
        ENTITY,
        PASSENGER;

        public static Direction fromString(String s) {
            if (s == null) return null;
            String v = s.trim().toLowerCase(Locale.ROOT);
            if (v.isEmpty()) return null;
            return switch (v) {
                case "entity" -> ENTITY;
                case "passenger" -> PASSENGER;
                default -> null;
            };
        }

        public String toBedrockString() {
            return this == PASSENGER ? "passenger" : "entity";
        }
    }
}
