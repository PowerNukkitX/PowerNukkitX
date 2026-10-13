package org.powernukkitx.level.generator.feature.placement;

import org.powernukkitx.level.generator.object.ObjectGenerator;

/**
 * Creates an object generator for one origin-level placement attempt.
 *
 * @author Curse
 */
@FunctionalInterface
public interface PlacementGeneratorProvider {

    /**
     * Returns the generator to execute, or null when this attempt has no child.
     */
    ObjectGenerator create(FeaturePlacementContext context);
}
