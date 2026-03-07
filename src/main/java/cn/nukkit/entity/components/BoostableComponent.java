package cn.nukkit.entity.components;

import cn.nukkit.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Bedrock component: {@code minecraft:boostable}.
 *
 * Defines boost mechanics for rideable entities (e.g. pigs, striders).
 * When the rider uses a configured boost item (such as a carrot on a stick),
 * the entity receives a temporary movement speed increase.
 *
 * <p><b>Properties:</b></p>
 * <ul>
 *   <li>{@code speed_multiplier}: movement speed multiplier applied during the boost.</li>
 *   <li>{@code duration}: duration of the boost effect.</li>
 *   <li>{@code boost_items}: list of items that can trigger the boost.</li>
 * </ul>
 * 
 * @author Curse
 */
public record BoostableComponent(Float speedMultiplier, Float duration, List<BoostItem> boostItems) {
    public BoostableComponent {
        if (speedMultiplier != null && !Float.isFinite(speedMultiplier)) speedMultiplier = null;
        if (duration != null && !Float.isFinite(duration)) duration = null;
        if (speedMultiplier != null) speedMultiplier = Math.max(0.0f, speedMultiplier);
        if (duration != null) duration = Math.max(0.0f, duration);

        if (boostItems != null) {
            if (boostItems.isEmpty()) {
                boostItems = null;
            } else {
                List<BoostItem> cleaned = new ArrayList<>(boostItems.size());
                for (BoostItem it : boostItems) {
                    if (it == null) continue;
                    BoostItem s = it.sanitized();
                    if (s != null) cleaned.add(s);
                }
                boostItems = cleaned.isEmpty() ? null : Collections.unmodifiableList(cleaned);
            }
        }
    }

    /** True when NOTHING is defined -> treat as "component not present". */
    public boolean isEmpty() {
        return speedMultiplier == null
                && duration == null
                && (boostItems == null || boostItems.isEmpty());
    }

    public float resolvedSpeedMultiplier() {
        return speedMultiplier != null ? speedMultiplier : 1.0f;
    }

    public float resolvedDuration() {
        return duration != null ? duration : 3.0f;
    }

    public List<BoostItem> resolvedBoostItems() {
        return boostItems != null ? boostItems : List.of();
    }

    public boolean hasBoostItems() {
        return boostItems != null && !boostItems.isEmpty();
    }

    public Set<String> resolvedBoostItemIds() {
        if (boostItems == null || boostItems.isEmpty()) return Collections.emptySet();

        LinkedHashSet<String> out = new LinkedHashSet<>();
        for (BoostItem it : boostItems) {
            if (it == null) continue;
            String id = it.item();
            if (id != null && !id.isEmpty()) {
                out.add(id);
            }
        }
        return out;
    }

    /** Resolves the boost entry for a held item id */
    public BoostItem resolveBoostItem(String heldItemId) {
        if (heldItemId == null || heldItemId.isEmpty()) return null;
        if (boostItems == null || boostItems.isEmpty()) return null;

        String key = heldItemId.trim();
        if (key.isEmpty()) return null;

        for (BoostItem it : boostItems) {
            if (it == null) continue;
            String id = it.item();
            if (id != null && id.equals(key)) return it;
        }
        return null;
    }

    public BoostItem resolveBoostItem(Item held) {
        if (held == null || held.isNull()) return null;
        return resolveBoostItem(held.getId());
    }

    public static BoostableComponent defaults() {
        return new BoostableComponent(null, null, null);
    }

    /**
     * - "item" is required; entries without it are discarded by sanitized()
     * - "damage" is durability damage per use (integer). If not set, resolve to 0 (no damage)
     * - "replace_item" is optional. If set, when the item breaks it becomes this item
     */
    public record BoostItem(String item, Integer damage, String replaceItem) {
        public BoostItem {
            item = norm(item);
            replaceItem = norm(replaceItem);

            if (damage != null && damage < 0) damage = 0;
        }

        public BoostItem sanitized() {
            if (item == null) return null;
            return this;
        }

        public int resolvedDamage() {
            return damage != null ? damage : 0;
        }

        public String resolvedReplaceItem() {
            return replaceItem;
        }

        public Item applyUse(Item held) {
            if (held == null) return Item.AIR;
            int dmg = resolvedDamage();
            if (dmg <= 0) return held;

            int max = held.getMaxDurability();
            if (max <= 0) return held;

            int newDamage = held.getDamage() + dmg;

            if (newDamage >= max) {
                String rep = resolvedReplaceItem();
                if (rep != null) {
                    Item replaced = Item.get(rep, 0, 1);
                    return replaced != null ? replaced : Item.AIR;
                }
                return Item.AIR;
            }

            held.setDamage(newDamage);
            return held;
        }

        private static String norm(String s) {
            if (s == null) return null;
            String v = s.trim().toLowerCase(Locale.ROOT);
            if (v.isEmpty()) return null;
            if (!v.contains(":")) v = "minecraft:" + v;
            return v;
        }
    }
}