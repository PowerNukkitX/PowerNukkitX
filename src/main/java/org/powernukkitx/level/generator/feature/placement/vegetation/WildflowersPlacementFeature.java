package org.powernukkitx.level.generator.feature.placement.vegetation;

import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockWildflowers;
import org.powernukkitx.block.property.enums.MinecraftCardinalDirection;
import org.powernukkitx.level.generator.feature.placement.FeaturePlacementContext;
import org.powernukkitx.level.generator.feature.placement.PlacementFeature;
import org.powernukkitx.level.generator.feature.placement.ScatterCoordinate;
import org.powernukkitx.level.generator.feature.placement.ScatterEvaluationOrder;
import org.powernukkitx.level.generator.feature.placement.ScatterPlacementFeature;
import org.powernukkitx.level.generator.feature.placement.VegetationPlacementFeature;
import org.powernukkitx.tags.BlockTags;

import static org.powernukkitx.block.property.CommonBlockProperties.GROWTH;
import static org.powernukkitx.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;

/**
 * Native-style wildflowers placement and local scatter.
 *
 * @author Curse
 */
public final class WildflowersPlacementFeature implements PlacementFeature {
    private static final WildflowersPlacementFeature INSTANCE = new WildflowersPlacementFeature();

    private static final PlacementFeature PLACEMENT = new VegetationPlacementFeature(
            WildflowersPlacementFeature::createState,
            WildflowersPlacementFeature::isAir,
            WildflowersPlacementFeature::canSurvive
    );

    private static final PlacementFeature SCATTER = new ScatterPlacementFeature(
            64,
            ScatterEvaluationOrder.ZYX,
            ScatterCoordinate.triangle(-6, 6),
            ScatterCoordinate.triangle(-2, 2),
            ScatterCoordinate.triangle(-6, 6),
            INSTANCE
    );

    private WildflowersPlacementFeature() {
    }

    /**
     * Returns the native local birch-wildflowers scatter feature.
     */
    public static PlacementFeature scatter() {
        return SCATTER;
    }

    @Override
    public boolean place(FeaturePlacementContext context) {
        return PLACEMENT.place(context);
    }

    private static BlockState createState(FeaturePlacementContext context) {
        int growth = context.getRandom().nextExclusiveInt(4);
        int direction = context.getRandom().nextExclusiveInt(4);

        return BlockWildflowers.PROPERTIES.getBlockState(
                MINECRAFT_CARDINAL_DIRECTION.createValue(MinecraftCardinalDirection.VALUES[direction]),
                GROWTH.createValue(growth)
        );
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
