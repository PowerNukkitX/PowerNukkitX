package org.powernukkitx.level.generator.feature.placement.vegetation;

import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockAzalea;
import org.powernukkitx.block.BlockFloweringAzalea;
import org.powernukkitx.block.BlockMossCarpet;
import org.powernukkitx.block.BlockShortGrass;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockTallGrass;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.level.generator.feature.placement.FeaturePlacementContext;
import org.powernukkitx.level.generator.feature.placement.PlacementFeature;
import org.powernukkitx.level.generator.feature.placement.WeightedPlacementFeature;
import org.powernukkitx.tags.BlockTags;

import java.util.List;

/**
 * Native moss-patch vegetation weighted child.
 *
 * @author Curse
 */
public final class MossVegetationPlacementFeature implements PlacementFeature {
    private static final BlockState FLOWERING_AZALEA = BlockFloweringAzalea.PROPERTIES.getDefaultState();
    private static final BlockState AZALEA = BlockAzalea.PROPERTIES.getDefaultState();
    private static final BlockState MOSS_CARPET = BlockMossCarpet.PROPERTIES.getDefaultState();
    private static final BlockState SHORT_GRASS = BlockShortGrass.PROPERTIES.getDefaultState();
    private static final BlockState LOWER_TALL_GRASS = BlockTallGrass.PROPERTIES.getBlockState(
            CommonBlockProperties.UPPER_BLOCK_BIT.createValue(false)
    );
    private static final BlockState UPPER_TALL_GRASS = BlockTallGrass.PROPERTIES.getBlockState(
            CommonBlockProperties.UPPER_BLOCK_BIT.createValue(true)
    );

    private static final PlacementFeature WEIGHTED = new WeightedPlacementFeature(
            List.of(
                    new WeightedPlacementFeature.Entry(
                            context -> placeDirtSupported(context, FLOWERING_AZALEA),
                            4
                    ),
                    new WeightedPlacementFeature.Entry(
                            context -> placeDirtSupported(context, AZALEA),
                            7
                    ),
                    new WeightedPlacementFeature.Entry(
                            MossVegetationPlacementFeature::placeMossCarpet,
                            25
                    ),
                    new WeightedPlacementFeature.Entry(
                            context -> placeDirtSupported(context, SHORT_GRASS),
                            50
                    ),
                    new WeightedPlacementFeature.Entry(
                            MossVegetationPlacementFeature::placeTallGrass,
                            10
                    )
            )
    );

    private static final MossVegetationPlacementFeature INSTANCE = new MossVegetationPlacementFeature();

    private MossVegetationPlacementFeature() {
    }

    /**
     * Returns the native moss vegetation child.
     */
    public static PlacementFeature feature() {
        return INSTANCE;
    }

    @Override
    public boolean place(FeaturePlacementContext context) {
        return WEIGHTED.place(context);
    }

    private static boolean placeDirtSupported(FeaturePlacementContext context, BlockState state) {
        if (!isAir(context)
                || !context.getRoot().getBlockIfCachedOrLoaded(
                        context.getX(),
                        context.getY() - 1,
                        context.getZ()
                ).hasTag(BlockTags.DIRT)) {
            return false;
        }

        context.getRoot().setBlockStateAt(context.getX(), context.getY(), context.getZ(), state);
        return true;
    }

    private static boolean placeMossCarpet(FeaturePlacementContext context) {
        if (!isAir(context)
                || context.getRoot().getBlockStateIfCachedOrLoaded(
                        context.getX(),
                        context.getY() - 1,
                        context.getZ()
                ) == BlockAir.STATE) {
            return false;
        }

        context.getRoot().setBlockStateAt(context.getX(), context.getY(), context.getZ(), MOSS_CARPET);
        return true;
    }

    private static boolean placeTallGrass(FeaturePlacementContext context) {
        int x = context.getX();
        int y = context.getY();
        int z = context.getZ();

        if (!isAir(context)
                || y + 1 >= context.getGenerationContext().getLevel().getMaxHeight()
                || context.getRoot().getBlockStateIfCachedOrLoaded(x, y + 1, z) != BlockAir.STATE
                || !context.getRoot().getBlockIfCachedOrLoaded(x, y - 1, z).hasTag(BlockTags.DIRT)) {
            return false;
        }

        context.getRoot().setBlockStateAt(x, y, z, LOWER_TALL_GRASS);
        context.getRoot().setBlockStateAt(x, y + 1, z, UPPER_TALL_GRASS);
        return true;
    }

    private static boolean isAir(FeaturePlacementContext context) {
        return context.getRoot().getBlockStateIfCachedOrLoaded(
                context.getX(),
                context.getY(),
                context.getZ()
        ) == BlockAir.STATE;
    }
}
