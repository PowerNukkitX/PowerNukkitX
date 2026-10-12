package org.powernukkitx.level.generator.feature.placement;

import com.google.common.base.Preconditions;

import java.util.List;

/**
 * Places children sequentially, propagating each successful output position.
 *
 * @author Curse
 */
public final class SequencePlacementFeature implements PlacementFeature {

    private final List<PlacementFeature> features;

    /**
     * Creates a sequence feature.
     */
    public SequencePlacementFeature(List<PlacementFeature> features) {
        Preconditions.checkNotNull(features, "features");
        Preconditions.checkArgument(features.size() > 0, "features cannot be empty");
        this.features = List.copyOf(features);
    }

    @Override
    public boolean place(FeaturePlacementContext context) {
        for (PlacementFeature feature : this.features) {
            if (!feature.place(context)) {
                return false;
            }
        }
        return true;
    }
}
