package cn.nukkit.entity.components;

import java.util.*;

/**
 * Bedrock component: {@code minecraft:home}.
 *
 * Defines a "home" location restriction for an entity. The home position
 * acts as an anchor point that limits how far the entity may move depending
 * on the configured restriction type.
 *
 * <p>The component may optionally define a list of block identifiers that
 * are considered valid home anchors. When present, the entity may establish
 * its home position relative to one of these blocks.</p>
 *
 * <p><b>Properties:</b></p>
 * <ul>
 *   <li>{@code home_block_list} — Optional list of block identifiers that
 *       can act as valid home anchors.</li>
 *   <li>{@code restriction_radius} — Radius around the home position within
 *       which the entity is allowed to move.</li>
 *   <li>{@code restriction_type} — Determines how movement is restricted:
 *       <ul>
 *         <li>{@code none} — No restriction is applied.</li>
 *         <li>{@code random_movement} — Random wandering is limited to the
 *             home radius.</li>
 *         <li>{@code all_movement} — All movement attempts outside the radius
 *             are restricted.</li>
 *       </ul>
 *   </li>
 * </ul>
 *
 * <p><b>Behavior requirements</b></p>
 * <p>This component does not enforce movement restrictions by itself.
 * A movement behavior such as
 * {@link cn.nukkit.entity.ai.executor.MoveToTargetExecutor}
 * must be present to guide the entity back toward its home position.</p>
 * 
 * @author Curse
 */
public record HomeComponent(boolean enabled, Set<String> homeBlockList, Integer restrictionRadius, RestrictionType restrictionType) {

    public HomeComponent(Integer restrictionRadius, RestrictionType restrictionType) {
        this(true, null, restrictionRadius, restrictionType);
    }
    public HomeComponent(Set<String> homeBlockList, Integer restrictionRadius, RestrictionType restrictionType) {
        this(true, homeBlockList, restrictionRadius, restrictionType);
    }

    public HomeComponent {
        restrictionRadius = (restrictionRadius == null) ? null : Math.max(0, restrictionRadius);

        if (homeBlockList == null || homeBlockList.isEmpty()) {
            homeBlockList = Collections.emptySet();
        } else {
            LinkedHashSet<String> out = new LinkedHashSet<>();
            for (String v : homeBlockList) {
                if (v == null) continue;
                String s = v.trim();
                if (s.isEmpty()) continue;
                out.add(s.toLowerCase());
            }
            homeBlockList = out.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(out);
        }

        restrictionType = (restrictionType == null) ? RestrictionType.NONE : restrictionType;
    }

    public static HomeComponent defaults() {
        return new HomeComponent(false, null, null, null);
    }

    public enum RestrictionType {
        NONE("none"),
        RANDOM_MOVEMENT("random_movement"),
        ALL_MOVEMENT("all_movement");

        private final String id;

        RestrictionType(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }

        public static RestrictionType from(String value) {
            if (value == null) return NONE;
            String v = value.trim().toLowerCase();
            for (RestrictionType t : values()) {
                if (t.id.equals(v)) return t;
            }
            return NONE;
        }
    }

    /**
     * Component presence rule:
     * - If enabled == true -> present even if other fields are empty/null
     * - Else present if any of: home_block_list provided, restriction_radius provided, restriction_type != none
     */
    public boolean isPresent() {
        if (enabled) return true;
        if (restrictionRadius != null) return true;
        if (restrictionType != RestrictionType.NONE) return true;
        return !homeBlockList.isEmpty();
    }

    public boolean hasHomeBlockList() {
        return !homeBlockList.isEmpty();
    }

    public boolean isRestricted() {
        return isPresent() && restrictionType != RestrictionType.NONE && (restrictionRadius == null || restrictionRadius > 0);
    }
}
