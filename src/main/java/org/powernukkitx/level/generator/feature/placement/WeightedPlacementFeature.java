package org.powernukkitx.level.generator.feature.placement;

import com.google.common.base.Preconditions;

import java.util.List;

/**
 * Selects exactly one child using relative feature weights.
 *
 * @author Curse
 */
public final class WeightedPlacementFeature implements PlacementFeature {

    private final List<Entry> entries;
    private final int totalWeight;

    /**
     * Creates a weighted placement feature.
     */
    public WeightedPlacementFeature(List<Entry> entries) {
        Preconditions.checkNotNull(entries, "entries");
        Preconditions.checkArgument(entries.size() > 0, "entries cannot be empty");

        this.entries = List.copyOf(entries);

        int totalWeight = 0;
        for (Entry entry : this.entries) {
            totalWeight = (int) ((float) totalWeight + entry.weight());
        }

        Preconditions.checkArgument(totalWeight > 0, "total weight must be > 0");
        this.totalWeight = totalWeight;
    }

    @Override
    public boolean place(FeaturePlacementContext context) {
        int selected = context.getRandom().nextExclusiveInt(this.totalWeight);

        for (Entry entry : this.entries) {
            selected = (int) ((float) selected - entry.weight());
            if (selected < 0) {
                return entry.feature().place(context);
            }
        }

        return false;
    }

    /**
     * One weighted child entry.
     */
    public record Entry(PlacementFeature feature, float weight) {

        /**
         * Creates a weighted child entry.
         */
        public Entry {
            Preconditions.checkNotNull(feature, "feature");
            Preconditions.checkArgument(Float.isFinite(weight) && weight > 0, "weight must be finite and positive");
        }
    }
}
