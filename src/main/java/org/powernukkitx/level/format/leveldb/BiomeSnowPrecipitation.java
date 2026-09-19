package org.powernukkitx.level.format.leveldb;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.format.BiomeState;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.registry.Registries;

import java.util.concurrent.ThreadLocalRandom;

import static org.powernukkitx.block.property.CommonBlockProperties.HEIGHT;

/**
 * Applies Bedrock live precipitation snow accumulation to ticking chunks.
 *
 * @author Curse
 */
final class BiomeSnowPrecipitation {
    private static final float MAX_SNOW_TEMPERATURE = 0.15f;
    private static final int MIN_UPDATE_FREQUENCY = 16;
    private static final int BASE_UPDATE_FREQUENCY = 256;
    private static final int UPDATE_FREQUENCY_RANGE = 512;
    private static final int[][] HORIZONTAL_OFFSETS = {
            {0, -1},
            {1, 0},
            {0, 1},
            {-1, 0}
    };

    private BiomeSnowPrecipitation() {
    }

    static void tick(Level level, IChunk chunk, int attempts, float rainLevel) {
        if (attempts <= 0 || rainLevel <= 0.0f) return;

        ThreadLocalRandom random = ThreadLocalRandom.current();
        int chunkBaseX = chunk.getX() << 4;
        int chunkBaseZ = chunk.getZ() << 4;

        for (int attempt = 0; attempt < attempts; attempt++) {
            int snowRandom = chunk.nextSnowRandomValue();
            int localX = snowRandom >>> 2 & 0x0f;
            int localZ = snowRandom >>> 10 & 0x0f;
            int rainY = chunk.getRainHeight(localX, localZ);
            if (!isInWorld(chunk, rainY)) continue;

            int biomeId = chunk.getBiomeId(localX, rainY, localZ);
            var registeredBiome = Registries.BIOME.get(biomeId);
            if (registeredBiome == null) continue;

            float downfallAmount = rainLevel * registeredBiome.second().getDownfall();
            if (downfallAmount <= 0.0f) continue;

            int updateFrequency = Math.max(
                    MIN_UPDATE_FREQUENCY,
                    (int) (BASE_UPDATE_FREQUENCY - downfallAmount * UPDATE_FREQUENCY_RANGE)
            );
            if (random.nextInt(updateFrequency) != 0) continue;

            tryToPlaceTopSnow(level, chunkBaseX + localX, rainY, chunkBaseZ + localZ);
        }
    }

    private static void tryToPlaceTopSnow(Level level, int worldX, int y, int worldZ) {
        SnowCandidate selected = getCandidate(level, worldX, y, worldZ);
        if (selected == null) return;

        SnowCandidate target = selected;
        int halfNewHeight = selected.newHeight() / 2;

        for (int[] offset : HORIZONTAL_OFFSETS) {
            SnowCandidate neighbor = getCandidate(level, worldX + offset[0], y, worldZ + offset[1]);
            if (neighbor == null || neighbor.newHeight() >= target.newHeight()) continue;

            if (isTopRainBlockPos(neighbor) || neighbor.newHeight() < halfNewHeight) {
                target = neighbor;
            }
        }

        BiomeSnowReconciler.reconcile(
                level,
                target.chunk(),
                target.localX(),
                target.y(),
                target.localZ(),
                target.depth() + 1
        );
    }

    private static SnowCandidate getCandidate(Level level, int worldX, int y, int worldZ) {
        IChunk chunk = level.getChunkIfLoaded(Math.floorDiv(worldX, 16), Math.floorDiv(worldZ, 16));
        if (chunk == null || !isInWorld(chunk, y)) return null;

        int localX = worldX & 0x0f;
        int localZ = worldZ & 0x0f;
        int biomeId = chunk.getBiomeId(localX, y, localZ);
        var registeredBiome = Registries.BIOME.get(biomeId);
        if (registeredBiome == null) return null;

        var biome = registeredBiome.second();
        var chunkGenData = biome.getChunkGenData();
        if (chunkGenData == null || chunkGenData.getClimate() == null) return null;

        int maxDepth = (int) (BiomeState.getEffectiveSnowAccumulationMax(chunkGenData.getClimate()) * 8.0f);
        if (maxDepth <= 0) return null;

        float temperature = BiomeSnowNoise.positionTemperature(biomeId, biome.getTemperature(), worldX, y, worldZ);
        if (temperature > MAX_SNOW_TEMPERATURE) return null;
        if (!BiomeSnowChunkReconciler.canAccumulateSnow(level, chunk, localX, y, localZ)) return null;

        int depth = BiomeSnowReconciler.getSnowDepth(chunk, localX, y, localZ);
        if (depth >= maxDepth) return null;

        BlockState target = chunk.getBlockState(localX, y, localZ);
        int newHeight = BlockID.SNOW_LAYER.equals(target.getIdentifier()) ? target.getPropertyValue(HEIGHT) + 2 : 1;
        return new SnowCandidate(chunk, localX, y, localZ, depth, newHeight);
    }

    private static boolean isTopRainBlockPos(SnowCandidate candidate) {
        return candidate.chunk().getRainHeight(candidate.localX(), candidate.localZ()) == candidate.y();
    }

    private static boolean isInWorld(IChunk chunk, int y) {
        return y >= chunk.getDimensionData().getMinHeight() && y <= chunk.getDimensionData().getMaxHeight();
    }

    private record SnowCandidate(IChunk chunk, int localX, int y, int localZ, int depth, int newHeight) {
    }
}
