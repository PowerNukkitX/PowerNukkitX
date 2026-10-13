package org.powernukkitx.level.generator.feature.placement.vegetation;

import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockShortGrass;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.generator.feature.placement.FeaturePlacementContext;
import org.powernukkitx.level.generator.feature.placement.PlacementFeature;
import org.powernukkitx.level.generator.feature.placement.ScatterCoordinate;
import org.powernukkitx.level.generator.feature.placement.ScatterEvaluationOrder;
import org.powernukkitx.level.generator.feature.placement.ScatterPlacementFeature;
import org.powernukkitx.level.generator.feature.placement.VegetationPlacementFeature;
import org.powernukkitx.tags.BlockTags;

/**
 * Native local tall-grass scatter attached to LegacyTreeFeature attempts.
 *
 * @author Curse
 */
public final class TallGrassAroundTreePlacementFeature implements PlacementFeature {
    private static final BlockState STATE = BlockShortGrass.PROPERTIES.getDefaultState();

    private static final TallGrassAroundTreePlacementFeature INSTANCE =
            new TallGrassAroundTreePlacementFeature();

    private static final PlacementFeature PLACEMENT = new VegetationPlacementFeature(
            context -> STATE,
            TallGrassAroundTreePlacementFeature::isAir,
            TallGrassAroundTreePlacementFeature::canSurvive
    );

    private static final PlacementFeature SCATTER = new ScatterPlacementFeature(
            23,
            ScatterEvaluationOrder.ZYX,
            ScatterCoordinate.gaussian(-3, 3),
            ScatterCoordinate.gaussian(-4, 4),
            ScatterCoordinate.gaussian(-3, 3),
            INSTANCE
    );

    private TallGrassAroundTreePlacementFeature() {
    }

    /**
     * Returns the single native tall-grass placement child.
     */
    public static PlacementFeature feature() {
        return INSTANCE;
    }

    /**
     * Returns the native tree-local tall-grass scatter.
     */
    public static PlacementFeature scatter() {
        return SCATTER;
    }

    @Override
    public boolean place(FeaturePlacementContext context) {
        return PLACEMENT.place(context);
    }

    private static boolean isAir(FeaturePlacementContext context) {
        return context.getRoot().getBlockStateIfCachedOrLoaded(
                context.getX(),
                context.getY(),
                context.getZ()
        ) == BlockAir.STATE;
    }

    private static boolean canSurvive(FeaturePlacementContext context) {
        return context.getRoot().getBlockIfCachedOrLoaded(
                context.getX(),
                context.getY() - 1,
                context.getZ()
        ).hasTag(BlockTags.DIRT);
    }
}
