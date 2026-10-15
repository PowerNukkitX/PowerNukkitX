package org.powernukkitx.level.generator.feature.tree;

import com.google.common.base.Preconditions;
import org.powernukkitx.level.generator.feature.placement.PlacementFeature;
import org.powernukkitx.utils.random.RandomSourceProvider;

/**
 * Selects one origin-level tree feature for a tree placement attempt.
 *
 * @author Curse
 */
@FunctionalInterface
public interface TreeFeatureSelector {

    /**
     * Selects the feature for the current attempt.
     */
    PlacementFeature select(RandomSourceProvider random);

    /**
     * Returns a selector which always chooses the same feature.
     */
    static TreeFeatureSelector fixed(PlacementFeature feature) {
        Preconditions.checkNotNull(feature, "feature");
        return random -> feature;
    }
}
