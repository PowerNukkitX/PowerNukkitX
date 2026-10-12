package org.powernukkitx.level.generator.feature.multi;

import org.powernukkitx.level.generator.feature.SurfaceCandidateFeature;
import org.powernukkitx.level.generator.feature.placement.AggregatePlacementFeature;
import org.powernukkitx.level.generator.feature.placement.GeneratorPlacementFeature;
import org.powernukkitx.level.generator.feature.placement.PlacementFeature;
import org.powernukkitx.level.generator.feature.placement.PlacementPredicate;
import org.powernukkitx.level.generator.feature.placement.ScatterCoordinate;
import org.powernukkitx.level.generator.feature.placement.ScatterEvaluationOrder;
import org.powernukkitx.level.generator.feature.placement.ScatterPlacementFeature;
import org.powernukkitx.level.generator.feature.placement.WeightedPlacementFeature;
import org.powernukkitx.level.generator.feature.placement.vegetation.LeafLitterPlacementFeature;
import org.powernukkitx.level.generator.feature.placement.vegetation.TallGrassAroundTreePlacementFeature;
import org.powernukkitx.level.generator.feature.tree.TreePlacementFeatures;
import org.powernukkitx.level.generator.object.ObjectBigMushroom;

import java.util.List;

public class RandomRoofedForestFeatureWithDecorationFeature extends SurfaceCandidateFeature {

    public static final String NAME = "minecraft:random_roofed_forest_feature_with_decoration_feature";

    private static final PlacementFeature HUGE_MUSHROOM = new GeneratorPlacementFeature(
            context -> new ObjectBigMushroom()
    );

    private static final PlacementFeature WEIGHTED_BASE = new WeightedPlacementFeature(
            List.of(
                    new WeightedPlacementFeature.Entry(HUGE_MUSHROOM, 75),
                    new WeightedPlacementFeature.Entry(TreePlacementFeatures.roofed(), 630),
                    new WeightedPlacementFeature.Entry(TreePlacementFeatures.birch(), 70),
                    new WeightedPlacementFeature.Entry(TreePlacementFeatures.fancyOak(), 30),
                    new WeightedPlacementFeature.Entry(TreePlacementFeatures.oak(), 195)
            )
    );

    private static final PlacementFeature WITH_LEAF_LITTER = new AggregatePlacementFeature(
            List.of(
                    WEIGHTED_BASE,
                    LeafLitterPlacementFeature.scatter()
            ),
            AggregatePlacementFeature.EarlyOut.FIRST_FAILURE
    );

    private static final PlacementFeature FOREST_FOLIAGE_TALL_GRASS = new ScatterPlacementFeature(
            20,
            ScatterEvaluationOrder.ZYX,
            ScatterCoordinate.gaussian(-3, 3),
            ScatterCoordinate.gaussian(-4, 4),
            ScatterCoordinate.gaussian(-3, 3),
            TallGrassAroundTreePlacementFeature.feature()
    );

    private static final PlacementFeature WITH_DECORATION = new AggregatePlacementFeature(
            List.of(
                    WITH_LEAF_LITTER,
                    FOREST_FOLIAGE_TALL_GRASS
            ),
            AggregatePlacementFeature.EarlyOut.NONE
    );

    @Override
    protected int iterations() {
        return 16;
    }

    @Override
    protected PlacementFeature placementFeature() {
        return WITH_DECORATION;
    }

    @Override
    protected PlacementPredicate candidatePredicate() {
        return TreePlacementFeatures::isRoofedForestCandidate;
    }

    @Override
    public String name() {
        return NAME;
    }
}
