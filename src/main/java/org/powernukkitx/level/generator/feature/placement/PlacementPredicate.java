package org.powernukkitx.level.generator.feature.placement;

/**
 * Tests whether an origin-level feature should be attempted.
 *
 * @author Curse
 */
@FunctionalInterface
public interface PlacementPredicate {

    /**
     * Returns whether the child feature should be attempted.
     */
    boolean test(FeaturePlacementContext context);
}
