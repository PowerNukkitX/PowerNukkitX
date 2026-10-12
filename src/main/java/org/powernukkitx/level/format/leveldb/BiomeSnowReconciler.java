package org.powernukkitx.level.format.leveldb;

import com.google.common.base.Preconditions;
import org.powernukkitx.block.BlockAir;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockSnow;
import org.powernukkitx.block.BlockSnowLayer;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.PrecipitationBehavior;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.IChunk;

import static org.powernukkitx.block.property.CommonBlockProperties.HEIGHT;

/**
 * Applies the BiomeState physical snow-column reconciliation.
 *
 * @author Curse
 */
final class BiomeSnowReconciler {
    private static final BlockState AIR = BlockAir.STATE;
    private static final BlockState SNOW = BlockSnow.PROPERTIES.getDefaultState();
    private static final BlockState SNOW_LAYER = BlockSnowLayer.PROPERTIES.getDefaultState();

    private BiomeSnowReconciler() {
    }

    static int getSnowDepth(IChunk chunk, int localX, int rainY, int localZ) {
        int y = rainY;
        BlockState state = getState(chunk, localX, y, localZ, 0);

        if (isAir(state) || isSnowLoggable(state)) {
            y--;
        }

        int depth = 0;
        int minY = chunk.getDimensionData().getMinHeight();

        while (y >= minY) {
            state = getState(chunk, localX, y, localZ, 0);
            if (isSnowLayer(state)) {
                depth += state.getPropertyValue(HEIGHT) + 1;
            } else if (BlockID.SNOW.equals(state.getIdentifier())) {
                depth += 8;
            } else {
                break;
            }
            y--;
        }

        return depth;
    }

    static void reconcile(Level level, IChunk chunk, int localX, int rainY, int localZ, int targetDepth) {
        Preconditions.checkArgument(localX >= 0 && localX < 16, "localX must be 0..15: %s", localX);
        Preconditions.checkArgument(localZ >= 0 && localZ < 16, "localZ must be 0..15: %s", localZ);
        Preconditions.checkArgument(targetDepth >= 0, "targetDepth must be non-negative: %s", targetDepth);

        int minY = chunk.getDimensionData().getMinHeight();
        int maxY = chunk.getDimensionData().getMaxHeight();
        int firstAirY = rainY;

        while (firstAirY <= maxY && !isAir(getState(chunk, localX, firstAirY, localZ, 0))) {
            firstAirY++;
        }

        int y = firstAirY - 1;
        int fullSnowBlocks = 0;

        while (y >= minY) {
            BlockState state = getState(chunk, localX, y, localZ, 0);
            if (!isSnowStackState(state)) break;
            if (BlockID.SNOW.equals(state.getIdentifier())) fullSnowBlocks++;
            y--;
        }

        if (y >= minY && !isSnowLoggable(getState(chunk, localX, y, localZ, 0))) {
            y++;
        }

        for (int i = 0; i < fullSnowBlocks && y <= maxY; i++, y++) {
            setState(level, chunk, localX, y, localZ, 0, SNOW);
        }

        int remainingDepth = targetDepth;
        while (remainingDepth > 0 && y <= maxY) {
            int units = Math.min(remainingDepth, 8);
            BlockState current = getState(chunk, localX, y, localZ, 0);
            BlockState snowLayer = isSnowLayer(current) ? current : SNOW_LAYER;
            snowLayer = snowLayer.setPropertyValue(BlockSnowLayer.PROPERTIES, HEIGHT, units - 1);

            if (!isAir(current) && !isSnowLayer(current) && isSnowLoggable(current)) {
                setState(level, chunk, localX, y, localZ, 1, current);
            }

            setState(level, chunk, localX, y, localZ, 0, snowLayer);
            remainingDepth -= units;
            y++;
        }

        while (y < firstAirY && y <= maxY) {
            clearPrimaryState(level, chunk, localX, y, localZ);
            y++;
        }
    }

    private static void clearPrimaryState(Level level, IChunk chunk, int x, int y, int z) {
        BlockState primary = getState(chunk, x, y, z, 0);
        BlockState secondary = getState(chunk, x, y, z, 1);

        if (isSnowLayer(primary) && !isAir(secondary)) {
            setState(level, chunk, x, y, z, 0, secondary);
            setState(level, chunk, x, y, z, 1, AIR);
            return;
        }

        setState(level, chunk, x, y, z, 0, AIR);
    }

    private static void setState(Level level, IChunk chunk, int x, int y, int z, int layer, BlockState state) {
        if (y < chunk.getDimensionData().getMinHeight() || y > chunk.getDimensionData().getMaxHeight()) return;

        BlockState current = chunk.getBlockState(x, y, z, layer);
        if (current.equals(state)) return;

        int worldX = (chunk.getX() << 4) + x;
        int worldZ = (chunk.getZ() << 4) + z;
        level.setBlockStateAt(worldX, y, worldZ, layer, state);
    }

    private static BlockState getState(IChunk chunk, int x, int y, int z, int layer) {
        if (y < chunk.getDimensionData().getMinHeight() || y > chunk.getDimensionData().getMaxHeight()) return AIR;
        return chunk.getBlockState(x, y, z, layer);
    }

    private static boolean isSnowStackState(BlockState state) {
        return isSnowLayer(state) || BlockID.SNOW.equals(state.getIdentifier());
    }

    private static boolean isSnowLayer(BlockState state) {
        return BlockID.SNOW_LAYER.equals(state.getIdentifier());
    }

    private static boolean isAir(BlockState state) {
        return state == AIR;
    }

    private static boolean isSnowLoggable(BlockState state) {
        PrecipitationBehavior behavior = state.toBlock().getPrecipitationBehavior();
        return behavior != null && behavior.isSnowLoggable();
    }
}
