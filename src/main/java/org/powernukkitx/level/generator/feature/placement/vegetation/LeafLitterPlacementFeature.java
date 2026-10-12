package org.powernukkitx.level.generator.feature.placement.vegetation;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockLeafLitter;
import org.powernukkitx.block.BlockLiquid;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.enums.MinecraftCardinalDirection;
import org.powernukkitx.level.generator.feature.placement.FeaturePlacementContext;
import org.powernukkitx.level.generator.feature.placement.PlacementFeature;
import org.powernukkitx.level.generator.feature.placement.ScatterCoordinate;
import org.powernukkitx.level.generator.feature.placement.ScatterEvaluationOrder;
import org.powernukkitx.level.generator.feature.placement.ScatterPlacementFeature;
import org.powernukkitx.level.generator.feature.placement.VegetationPlacementFeature;
import org.powernukkitx.level.generator.object.BlockManager;
import org.powernukkitx.math.BlockFace;

import static org.powernukkitx.block.property.CommonBlockProperties.GROWTH;
import static org.powernukkitx.block.property.CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION;

/**
 * Native-style leaf-litter placement and local scatter.
 *
 * @author Curse
 */
public final class LeafLitterPlacementFeature implements PlacementFeature {
    private static final LeafLitterPlacementFeature INSTANCE = new LeafLitterPlacementFeature();

    private static final PlacementFeature PLACEMENT = new VegetationPlacementFeature(
            LeafLitterPlacementFeature::createState,
            LeafLitterPlacementFeature::isAir,
            LeafLitterPlacementFeature::canSurvive
    );

    private static final PlacementFeature SCATTER = new ScatterPlacementFeature(
            96,
            ScatterEvaluationOrder.ZXY,
            ScatterCoordinate.gaussian(-4, 4),
            ScatterCoordinate.gaussian(-2, 2),
            ScatterCoordinate.gaussian(-4, 4),
            INSTANCE
    );

    private LeafLitterPlacementFeature() {
    }

    /**
     * Returns the single leaf-litter placement feature.
     */
    public static PlacementFeature feature() {
        return INSTANCE;
    }

    /**
     * Returns the native local leaf-litter scatter feature.
     */
    public static PlacementFeature scatter() {
        return SCATTER;
    }

    /**
     * Removes queued leaf litter that no longer survives against the final feature root.
     */
    public static void pruneInvalidPlacements(BlockManager root) {
        for (Block block : root.getBlocks()) {
            if (!(block instanceof BlockLeafLitter)) {
                continue;
            }

            int x = block.getFloorX();
            int y = block.getFloorY();
            int z = block.getFloorZ();

            if (!canSurvive(root, x, y, z)) {
                root.unsetBlockStateAt(x, y, z, 0);
            }
        }
    }

    @Override
    public boolean place(FeaturePlacementContext context) {
        return PLACEMENT.place(context);
    }

    private static BlockState createState(FeaturePlacementContext context) {
        int growth = context.getRandom().nextExclusiveInt(4);
        int direction = context.getRandom().nextExclusiveInt(4);

        return BlockLeafLitter.PROPERTIES.getBlockState(
                MINECRAFT_CARDINAL_DIRECTION.createValue(MinecraftCardinalDirection.VALUES[direction]),
                GROWTH.createValue(growth)
        );
    }

    private static boolean isAir(FeaturePlacementContext context) {
        Block target = context.getRoot().getBlockIfQueuedOrLoaded(
                context.getX(),
                context.getY(),
                context.getZ()
        );
        return target.getBlockState() == BlockAir.STATE;
    }

    private static boolean canSurvive(FeaturePlacementContext context) {
        return canSurvive(
                context.getRoot(),
                context.getX(),
                context.getY(),
                context.getZ()
        );
    }

    private static boolean canSurvive(BlockManager root, int x, int y, int z) {
        if (root.getBlockIfQueuedOrLoaded(x, y, z, 1) instanceof BlockLiquid) {
            return false;
        }

        return BlockLeafLitter.isSupportValid(
                root.getBlockIfQueuedOrLoaded(x, y - 1, z),
                BlockFace.UP
        );
    }
}
