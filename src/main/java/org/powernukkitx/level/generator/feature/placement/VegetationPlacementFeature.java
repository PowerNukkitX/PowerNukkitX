package org.powernukkitx.level.generator.feature.placement;

import com.google.common.base.Preconditions;
import org.powernukkitx.block.BlockState;

/**
 * Places one locally configured vegetation block.
 *
 * @author Curse
 */
public final class VegetationPlacementFeature implements PlacementFeature {

    private final PlacementBlockStateProvider stateProvider;
    private final PlacementPredicate targetPredicate;
    private final PlacementPredicate survivalPredicate;

    /**
     * Creates a vegetation placement feature.
     */
    public VegetationPlacementFeature(
            PlacementBlockStateProvider stateProvider,
            PlacementPredicate targetPredicate,
            PlacementPredicate survivalPredicate
    ) {
        this.stateProvider = Preconditions.checkNotNull(stateProvider, "stateProvider");
        this.targetPredicate = Preconditions.checkNotNull(targetPredicate, "targetPredicate");
        this.survivalPredicate = Preconditions.checkNotNull(survivalPredicate, "survivalPredicate");
    }

    @Override
    public boolean place(FeaturePlacementContext context) {
        BlockState state = this.stateProvider.get(context);

        if (!this.targetPredicate.test(context) || !this.survivalPredicate.test(context)) {
            return false;
        }

        context.getRoot().setBlockStateAt(context.getX(), context.getY(), context.getZ(), state);
        return true;
    }
}
