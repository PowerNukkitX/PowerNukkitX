package cn.nukkit.entity.components;

import cn.nukkit.entity.EntityFilter;

import java.util.*;

/**
 * Bedrock component: {@code minecraft:healable}.
 *
 * Defines items that can heal an entity when used by a player.
 *
 * <p>Each heal entry may define a heal amount and optional status effects
 * applied to the entity. This component may also define interaction filters
 * and a {@code force_use} flag to allow usage even when the entity would
 * normally reject the interaction.</p>
 *
 * <p><b>Properties:</b></p>
 * <ul>
 *   <li>{@code filters} — Optional gate conditions for when healing is allowed.</li>
 *   <li>{@code force_use} — If true, healing items may be used even when not normally allowed (default: false).</li>
 *   <li>{@code items} — List of heal entries (item id, heal amount, optional effects).</li>
 * </ul>
 *
 * <p><b>Item entries</b></p>
 * <ul>
 *   <li>{@code item} — Item identifier (normalized to {@code minecraft:...} when missing a namespace).</li>
 *   <li>{@code heal_amount} — Amount healed (default: 1).</li>
 *   <li>{@code effects} — Optional list of status effects applied on use.</li>
 * </ul>
 *
 * <p><b>Effect entries</b></p>
 * <ul>
 *   <li>{@code name} — Effect identifier/name.</li>
 *   <li>{@code chance} — Chance to apply (0+; null means default behavior).</li>
 *   <li>{@code duration} — Duration in ticks/seconds depending on the consumer (0+).</li>
 *   <li>{@code amplifier} — Effect amplifier/level (0+).</li>
 * </ul>
 * 
 * @author Curse
 */
public record HealableComponent(EntityFilter filters, Boolean forceUse, List<Item> items) {

    public HealableComponent {
        if (items != null) {
            ArrayList<Item> out = new ArrayList<>(items.size());
            for (Item it : items) {
                if (it == null || it.isEmpty()) continue;
                out.add(it);
            }
            items = out.isEmpty() ? null : Collections.unmodifiableList(out);
        }
    }

    public HealableComponent(List<Item> items) {
        this(null, null, items);
    }

    /** True when NOTHING is defined -> treat as "component not present". */
    public boolean isEmpty() {
        return filters == null
                && forceUse == null
                && items == null;
    }

    public boolean resolvedForceUse() {
        return forceUse != null ? forceUse : false;
    }

    public boolean hasItems() {
        return items != null && !items.isEmpty();
    }

    public Set<String> resolvedHealableItems() {
        if (items == null || items.isEmpty()) return Collections.emptySet();

        LinkedHashSet<String> out = new LinkedHashSet<>();
        for (Item it : items) {
            if (it != null && it.item() != null) {
                out.add(it.item());
            }
        }
        return out;
    }

    public List<Item> resolvedItems() {
        return items != null ? items : Collections.emptyList();
    }

    public static HealableComponent defaults() {
        return new HealableComponent(null, null, null);
    }

    public record Item(String item, Integer healAmount, List<Effect> effects) {

        public Item(String item, Integer healAmount) {
            this(item, healAmount, null);
        }

        public Item {
            if (item != null) {
                String s = item.trim().toLowerCase(Locale.ROOT);
                if (s.isEmpty()) {
                    item = null;
                } else {
                    if (!s.contains(":")) s = "minecraft:" + s;
                    item = s;
                }
            }

            if (healAmount != null) {
                healAmount = Math.max(0, healAmount);
            }

            // Effects
            if (effects != null) {
                ArrayList<Effect> out = new ArrayList<>(effects.size());
                for (Effect e : effects) {
                    if (e == null || e.isEmpty()) continue;
                    out.add(e);
                }
                effects = out.isEmpty() ? null : Collections.unmodifiableList(out);
            }
        }

        public boolean isEmpty() {
            return item == null;
        }

        public int resolvedHealAmount() {
            return healAmount != null ? healAmount : 1;
        }

        public List<Effect> resolvedEffects() {
            return effects != null ? effects : Collections.emptyList();
        }
    }

    public record Effect(String name, Float chance, Float duration, Float amplifier) {
        public Effect {
            if (name != null) {
                String s = name.trim().toLowerCase(Locale.ROOT);
                name = s.isEmpty() ? null : s;
            }

            if (chance != null && !Float.isFinite(chance)) chance = null;
            if (duration != null && !Float.isFinite(duration)) duration = null;
            if (amplifier != null && !Float.isFinite(amplifier)) amplifier = null;

            if (chance != null) chance = Math.max(0.0f, chance);
            if (duration != null) duration = Math.max(0.0f, duration);
            if (amplifier != null) amplifier = Math.max(0.0f, amplifier);
        }

        public boolean isEmpty() {
            return name == null
                    && chance == null
                    && duration == null
                    && amplifier == null;
        }
    }
}
