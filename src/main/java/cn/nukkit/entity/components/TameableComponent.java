package cn.nukkit.entity.components;

import java.util.*;

/**
 * Bedrock component: {@code minecraft:tameable}.
 *
 * Defines whether an entity can be tamed by a player and which items
 * can be used to perform the taming action.
 *
 * <p>Taming typically requires the player to repeatedly use a valid
 * taming item on the entity until the taming attempt succeeds based
 * on the configured probability.</p>
 *
 * <p><b>Properties:</b></p>
 * <ul>
 *   <li>{@code probability} — Chance of a successful taming attempt per item use.
 *       Value is clamped to the range {@code [0.0, 1.0]}. Default: {@code 1.0}.</li>
 *   <li>{@code tame_items} — Optional list of item identifiers that can be used
 *       to tame the entity. If empty or not defined, taming items must be handled
 *       by other logic.</li>
 * </ul>
 */
public record TameableComponent(Float probability, Set<String> tameItems) {
    public TameableComponent {
        if (probability != null && !Float.isFinite(probability)) probability = null;
        if (probability != null) {
            float p = probability;
            if (p < 0.0f) p = 0.0f;
            if (p > 1.0f) p = 1.0f;
            probability = p;
        }

        if (tameItems != null) {
            LinkedHashSet<String> out = new LinkedHashSet<>();
            for (String it : tameItems) {
                if (it == null) continue;

                String s = it.trim().toLowerCase(Locale.ROOT);
                if (s.isEmpty()) continue;

                if (!s.contains(":")) {
                    s = "minecraft:" + s;
                }

                out.add(s);
            }

            tameItems = out.isEmpty() ? null : Collections.unmodifiableSet(out);
        }
    }

    /** True when NOTHING is defined -> treat as "component not present". */
    public boolean isEmpty() {
        return probability == null
                && tameItems == null;
    }

    public float resolvedProbability() {
        return probability != null ? probability : 1.0f;
    }

    public Set<String> resolvedTameItems() {
        return tameItems != null ? tameItems : Collections.emptySet();
    }

    public static TameableComponent defaults() {
        return new TameableComponent(null, null);
    }
}
