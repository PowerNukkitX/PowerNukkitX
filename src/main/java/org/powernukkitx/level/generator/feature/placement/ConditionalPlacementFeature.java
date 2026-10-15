package org.powernukkitx.level.generator.feature.placement;

import com.google.common.base.Preconditions;

/**
 * Places a child only when its predicate succeeds.
 *
 * @author Curse
 */
public final class ConditionalPlacementFeature implements PlacementFeature {

    private final PlacementPredicate predicate;
    private final PlacementFeature child;

    /**
     * Creates a conditional placement feature.
     */
    public ConditionalPlacementFeature(PlacementPredicate predicate, PlacementFeature child) {
        this.predicate = Preconditions.checkNotNull(predicate, "predicate");
        this.child = Preconditions.checkNotNull(child, "child");
    }

    @Override
    public boolean place(FeaturePlacementContext context) {
        return this.predicate.test(context) && this.child.place(context);
    }
}
