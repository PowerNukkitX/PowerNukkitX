package org.powernukkitx.level.format.leveldb;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.PrecipitationBehavior;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.format.BiomeState;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.registry.Registries;

/**
 * Reconciles physical biome snow for every column of one chunk.
 *
 * @author Curse
 */
final class BiomeSnowChunkReconciler {
    private static final float MAX_SNOW_TEMPERATURE = 0.15f;

    private BiomeSnowChunkReconciler() {
    }

    static void reconcile(Level level, IChunk chunk) {
        int chunkBaseX = chunk.getX() << 4;
        int chunkBaseZ = chunk.getZ() << 4;

        for (int z = 0; z < 16; z++) {
            int worldZ = chunkBaseZ + z;

            for (int x = 0; x < 16; x++) {
                int worldX = chunkBaseX + x;
                int rainY = chunk.getRainHeight(x, z);
                if (!isInWorld(chunk, rainY)) continue;

                int biomeId = chunk.getBiomeId(x, rainY, z);
                var registeredBiome = Registries.BIOME.get(biomeId);
                if (registeredBiome == null) continue;

                var biome = registeredBiome.second();
                var chunkGenData = biome.getChunkGenData();
                if (chunkGenData == null || chunkGenData.getClimate() == null) continue;

                var climate = chunkGenData.getClimate();
                if (BiomeState.getEffectiveSnowAccumulationMax(climate) <= 0.0f) continue;

                float temperature = BiomeSnowNoise.positionTemperature(
                        biomeId, biome.getTemperature(), worldX, rainY, worldZ
                );
                if (temperature > MAX_SNOW_TEMPERATURE || !canAccumulateSnow(level, chunk, x, rainY, z)) continue;

                int minimumUnits = (int) (climate.getSnowAccumulationMin() * 8.0f);
                int depth = (int) Math.ceil(minimumUnits * BiomeSnowNoise.snowDepthFactor(worldX, rainY, worldZ));

                depth = adjustDepth(depth, rainY, getRainHeight(level, worldX, worldZ - 1));
                depth = adjustDepth(depth, rainY, getRainHeight(level, worldX + 1, worldZ));
                depth = adjustDepth(depth, rainY, getRainHeight(level, worldX, worldZ + 1));
                depth = adjustDepth(depth, rainY, getRainHeight(level, worldX - 1, worldZ));

                BiomeSnowReconciler.reconcile(level, chunk, x, rainY, z, Math.max(depth, 0));
            }
        }
    }

    static boolean canAccumulateSnow(Level level, IChunk chunk, int x, int y, int z) {
        if (!isInWorld(chunk, y) || chunk.getBlockLight(x, y, z) > 11) return false;

        BlockState target = chunk.getBlockState(x, y, z);
        PrecipitationBehavior targetBehavior = target.toBlock().getPrecipitationBehavior();
        if (!BlockID.AIR.equals(target.getIdentifier())
                && !BlockID.SNOW_LAYER.equals(target.getIdentifier())
                && (targetBehavior == null || !targetBehavior.isSnowLoggable())) {
            return false;
        }

        int supportY = y - 1;
        if (!isInWorld(chunk, supportY)) return false;

        BlockState supportState = chunk.getBlockState(x, supportY, z);
        var support = supportState.toBlock(new Position(
                (chunk.getX() << 4) + x,
                supportY,
                (chunk.getZ() << 4) + z,
                level
        ));

        PrecipitationBehavior supportBehavior = support.getPrecipitationBehavior();
        if (supportBehavior != null && !supportBehavior.accumulatesSnow()) return false;
        return support.isSolid(BlockFace.UP);
    }

    private static int adjustDepth(int depth, int rainY, int neighborRainY) {
        if (neighborRainY > rainY) {
            return depth + 1;
        }
        if (rainY > neighborRainY && depth >= 2) {
            return depth - 1;
        }
        return depth;
    }

    private static int getRainHeight(Level level, int worldX, int worldZ) {
        IChunk chunk = level.getChunkIfLoaded(Math.floorDiv(worldX, 16), Math.floorDiv(worldZ, 16));
        if (chunk == null) return -1;
        return chunk.getRainHeight(worldX & 15, worldZ & 15);
    }

    private static boolean isInWorld(IChunk chunk, int y) {
        return y >= chunk.getDimensionData().getMinHeight() && y <= chunk.getDimensionData().getMaxHeight();
    }
}
