package org.powernukkitx.level.generator.feature.placement;

/**
 * Places one feature at the current placement-context position.
 *
 * <p>On success an implementation may update the context position to the
 * feature's output position.</p>
 *
 * @author Curse
 */
@FunctionalInterface
public interface PlacementFeature {

    /**
     * Places the feature and returns whether placement succeeded.
     */
    boolean place(FeaturePlacementContext context);
}
