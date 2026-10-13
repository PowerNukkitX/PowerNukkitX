package org.powernukkitx.level.generator.feature.placement;

import com.google.common.base.Preconditions;
import org.powernukkitx.level.generator.object.ObjectGenerator;
import org.powernukkitx.math.Vector3;

/**
 * Executes an existing object generator as an origin-level placement feature.
 *
 * @author Curse
 */
public final class GeneratorPlacementFeature implements PlacementFeature {

    private final PlacementGeneratorProvider provider;

    /**
     * Creates a generator-backed placement feature.
     */
    public GeneratorPlacementFeature(PlacementGeneratorProvider provider) {
        this.provider = Preconditions.checkNotNull(provider, "provider");
    }

    @Override
    public boolean place(FeaturePlacementContext context) {
        ObjectGenerator generator = this.provider.create(context);
        if (generator == null) {
            return false;
        }

        return generator.generate(
                context.getRoot(),
                context.getRandom(),
                new Vector3(context.getX(), context.getY(), context.getZ())
        );
    }
}
