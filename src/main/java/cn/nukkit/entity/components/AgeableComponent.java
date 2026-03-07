package cn.nukkit.entity.components;

import cn.nukkit.entity.EntityFilter;

import java.util.*;

/**
 * Bedrock component: {@code minecraft:ageable}.
 *
 * Defines the growth lifecycle of an entity from baby to adult.
 *
 * <p><b>Properties:</b></p>
 * <ul>
 *   <li>{@code duration}: time (in seconds) until the entity becomes an adult.
 *       Use {@code -1} to indicate the entity never grows up.</li>
 *   <li>{@code feed_items}: items that accelerate growth by reducing the remaining duration.</li>
 *   <li>{@code pause_growth_items}: items that pause growth when used.</li>
 *   <li>{@code reset_growth_items}: items that reset the growth timer when used.</li>
 *   <li>{@code drop_items}: items dropped when the entity grows up.</li>
 *   <li>{@code interact_filters}: optional filter gating when age interactions are allowed.</li>
 * </ul>
 * 
 * @author Curse
 */
public record AgeableComponent(EntityFilter interactFilters, Float duration, List<FeedItem> feedItems, Set<String> pauseGrowthItems, Set<String> resetGrowthItems, Set<String> dropItems) {

    public AgeableComponent {
        // Duration
        if (duration != null) {
            if (!Float.isFinite(duration)) {
                duration = null;
            } else {
                // allow -1 (never grow), otherwise clamp to >= 0
                duration = (duration < -1.0f) ? -1.0f : Math.max(0.0f, duration);
            }
        }

        // Feed items
        if (feedItems != null) {
            ArrayList<FeedItem> out = new ArrayList<>(feedItems.size());
            for (FeedItem it : feedItems) {
                if (it == null || it.isEmpty()) continue;
                out.add(it);
            }
            feedItems = out.isEmpty() ? null : Collections.unmodifiableList(out);
        }

        // Normalize item sets
        pauseGrowthItems = normalizeItemSet(pauseGrowthItems);
        resetGrowthItems = normalizeItemSet(resetGrowthItems);
        dropItems = normalizeItemSet(dropItems);
    }

    public AgeableComponent(Float duration) {
        this(null, duration, null, null, null, null);
    }

    public AgeableComponent(Float duration, List<FeedItem> feedItems) {
        this(null, duration, feedItems, null, null, null);
    }

    /** True when NOTHING is defined -> treat as "component not present". */
    public boolean isEmpty() {
        return interactFilters == null
                && duration == null
                && feedItems == null
                && pauseGrowthItems == null
                && resetGrowthItems == null
                && dropItems == null;
    }

    public float resolvedDuration() {
        return duration != null ? duration : 1200.0f;
    }

    /** Returns true if duration is -1 (never grow up). */
    public boolean resolvedNeverGrows() {
        return resolvedDuration() == -1.0f;
    }

    /** Returns true if feedItems is defined and non-empty. */
    public boolean hasFeedItems() {
        return feedItems != null && !feedItems.isEmpty();
    }

    public List<FeedItem> resolvedFeedItems() {
        return feedItems != null ? feedItems : Collections.emptyList();
    }

    public Set<String> resolvedFeedableItems() {
        if (feedItems == null || feedItems.isEmpty()) return Collections.emptySet();

        LinkedHashSet<String> out = new LinkedHashSet<>();
        for (FeedItem it : feedItems) {
            if (it != null && it.item() != null) {
                out.add(it.item());
            }
        }
        return out;
    }

    /** Convenience view of just the item ids. */
    public Set<String> resolvedFeedItemIds() {
        if (feedItems == null || feedItems.isEmpty()) return Collections.emptySet();

        LinkedHashSet<String> out = new LinkedHashSet<>();
        for (FeedItem it : feedItems) {
            if (it != null && it.item() != null) out.add(it.item());
        }
        return out;
    }

    public Set<String> resolvedPauseGrowthItems() {
        return pauseGrowthItems != null ? pauseGrowthItems : Collections.emptySet();
    }

    public Set<String> resolvedResetGrowthItems() {
        return resetGrowthItems != null ? resetGrowthItems : Collections.emptySet();
    }

    public Set<String> resolvedDropItems() {
        return dropItems != null ? dropItems : Collections.emptySet();
    }

    public static AgeableComponent defaults() {
        return new AgeableComponent(null, null, null, null, null, null);
    }

    /**
     * Feed entry used by Ageable feed items.
     *
     * Growth: percent of TOTAL duration to reduce when fed.
     * Example: 0.15 means reduce 15% of total duration.
     */
    public record FeedItem(String item, Float growth) {

        public FeedItem(String item) {
            this(item, 0.10f);
        }

        public FeedItem {
            item = normalizeItemId(item);
            if (growth != null) {
                if (!Float.isFinite(growth)) {
                    growth = null;
                } else {
                    growth = Math.max(0.0f, growth);
                }
            }
        }

        public boolean isEmpty() {
            return item == null;
        }

        public Float resolvedGrowth() {
            return growth;
        }
    }

    private static Set<String> normalizeItemSet(Set<String> in) {
        if (in == null || in.isEmpty()) return null;

        LinkedHashSet<String> out = new LinkedHashSet<>(Math.max(16, in.size()));
        for (String s : in) {
            String id = normalizeItemId(s);
            if (id != null) out.add(id);
        }
        return out.isEmpty() ? null : Collections.unmodifiableSet(out);
    }

    private static String normalizeItemId(String item) {
        if (item == null) return null;

        String s = item.trim().toLowerCase(Locale.ROOT);
        if (s.isEmpty()) return null;

        if (!s.contains(":")) s = "minecraft:" + s;
        return s;
    }
}