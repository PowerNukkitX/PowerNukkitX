package org.powernukkitx.level.generator.feature.placement;

import com.google.common.base.Preconditions;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockLiquid;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.Level;

/**
 * Snaps an air feature origin downward onto the nearest floor.
 *
 * @author Curse
 */
public final class SnapToFloorPlacementFeature implements PlacementFeature {

    private final int searchRange;
    private final PlacementFeature child;

    /**
     * Creates a floor-snap feature with the supplied vertical search range.
     */
    public SnapToFloorPlacementFeature(int searchRange, PlacementFeature child) {
        Preconditions.checkArgument(searchRange >= 0, "searchRange must be >= 0");
        this.searchRange = searchRange;
        this.child = Preconditions.checkNotNull(child, "child");
    }

    @Override
    public boolean place(FeaturePlacementContext context) {
        int inputX = context.getX();
        int inputY = context.getY();
        int inputZ = context.getZ();

        if (context.getRoot().getBlockStateIfCachedOrLoaded(inputX, inputY, inputZ) != BlockAir.STATE) {
            return false;
        }

        Level level = context.getGenerationContext().getLevel();
        int inputBiome = level.getBiomeId(inputX, inputY, inputZ);

        for (int distance = 1; distance <= this.searchRange; distance++) {
            int floorY = inputY - distance;
            if (floorY < level.getMinHeight()) {
                break;
            }

            BlockState state = context.getRoot().getBlockStateIfCachedOrLoaded(inputX, floorY, inputZ);
            if (state == BlockAir.STATE) {
                continue;
            }

            Block floor = context.getRoot().getBlockIfCachedOrLoaded(inputX, floorY, inputZ);
            if (floor instanceof BlockLiquid) {
                return false;
            }

            int snappedY = floorY + 1;
            if (level.getBiomeId(inputX, snappedY, inputZ) != inputBiome) {
                return false;
            }

            context.setPosition(inputX, snappedY, inputZ);
            if (this.child.place(context)) {
                return true;
            }

            context.setPosition(inputX, inputY, inputZ);
            return false;
        }

        return false;
    }
}
