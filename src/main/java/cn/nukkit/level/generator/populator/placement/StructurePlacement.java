package cn.nukkit.level.generator.populator.placement;

import cn.nukkit.level.Level;
import cn.nukkit.level.generator.biome.BiomePicker;
import cn.nukkit.level.generator.biome.OverworldBiomePicker;
import cn.nukkit.math.ChunkVector2;
import cn.nukkit.utils.random.RandomSourceProvider;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.function.Function;

import static cn.nukkit.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

@AllArgsConstructor
public class StructurePlacement {

    protected final PlacementSettings settings;

    public boolean canGenerate(long levelSeed, RandomSourceProvider random, int chunkX, int chunkZ, int biome) {
        int MAX_DISTANCE = settings.maxDistance;
        int MIN_DISTANCE = settings.minDistance;
        int regionX = regionCoord(chunkX, MAX_DISTANCE);
        int regionZ = regionCoord(chunkZ, MAX_DISTANCE);
        random.setSeed((levelSeed ^ settings.salt) + Level.chunkHash(regionX, regionZ));
        return isValidBiome(biome)
                && regionX * MAX_DISTANCE + random.nextBoundedInt(MAX_DISTANCE - MIN_DISTANCE) == chunkX
                && regionZ * MAX_DISTANCE + random.nextBoundedInt(MAX_DISTANCE - MIN_DISTANCE) == chunkZ;
    }

    public boolean canGenerate(long levelSeed, RandomSourceProvider random, int chunkX, int chunkZ, BiomePicker<?> biomePicker) {
        int sampleX = (chunkX << 4) + 7;
        int sampleZ = (chunkZ << 4) + 7;
        int biome;
        if (biomePicker instanceof OverworldBiomePicker overworldBiomePicker) {
            biome = overworldBiomePicker.pickRaw(sampleX, SEA_LEVEL, sampleZ).getBiomeId();
        } else {
            biome = biomePicker.pick(sampleX, SEA_LEVEL, sampleZ).getBiomeId();
        }
        return canGenerate(levelSeed, random, chunkX, chunkZ, biome);
    }

    public boolean isValidBiome(int biome) {
        return settings.isBiomeValid.apply(biome);
    }

    public ChunkVector2 findNearestGenerationChunk(ChunkVector2 origen, RandomSourceProvider random, BiomePicker<?> biomePicker, int radius) {
        int MAX_DISTANCE = settings.maxDistance;
        int MIN_DISTANCE = settings.minDistance;
        int spread = MAX_DISTANCE - MIN_DISTANCE;
        if (random == null || biomePicker == null || radius < 0 || MAX_DISTANCE <= 0 || spread < 0) {
            return null;
        }

        long levelSeed = random.getSeed();
        ChunkVector2 nearest = null;
        long nearestDistanceSq = Long.MAX_VALUE;
        int originX = origen.getX();
        int originZ = origen.getZ();

        int minChunkX = originX - radius;
        int maxChunkX = originX + radius;
        int minChunkZ = originZ - radius;
        int maxChunkZ = originZ + radius;

        int minRegionX = regionCoord(minChunkX, MAX_DISTANCE);
        int maxRegionX = regionCoord(maxChunkX, MAX_DISTANCE);
        int minRegionZ = regionCoord(minChunkZ, MAX_DISTANCE);
        int maxRegionZ = regionCoord(maxChunkZ, MAX_DISTANCE);

        for (int regionX = minRegionX; regionX <= maxRegionX; regionX++) {
            int originRegionX = regionX * MAX_DISTANCE;
            for (int regionZ = minRegionZ; regionZ <= maxRegionZ; regionZ++) {
                int originRegionZ = regionZ * MAX_DISTANCE;

                random.setSeed((levelSeed ^ settings.salt) + Level.chunkHash(regionX, regionZ));
                int chunkX = originRegionX + random.nextBoundedInt(spread);
                int chunkZ = originRegionZ + random.nextBoundedInt(spread);
                if (chunkX < minChunkX || chunkX > maxChunkX || chunkZ < minChunkZ || chunkZ > maxChunkZ) {
                    continue;
                }

                if (!canGenerate(levelSeed, random, chunkX, chunkZ, biomePicker)) {
                    continue;
                }

                long dx = (long) chunkX - originX;
                long dz = (long) chunkZ - originZ;
                long distanceSq = dx * dx + dz * dz;
                if (distanceSq < nearestDistanceSq
                        || (distanceSq == nearestDistanceSq && (nearest == null
                        || chunkX < nearest.getX()
                        || (chunkX == nearest.getX() && chunkZ < nearest.getZ())))) {
                    nearestDistanceSq = distanceSq;
                    nearest = new ChunkVector2(chunkX, chunkZ);
                }
            }
        }

        random.setSeed(levelSeed);
        return nearest;
    }

    protected static int regionCoord(int chunk, int spacing) {
        return chunk < 0 ? (chunk - spacing - 1) / spacing : chunk / spacing;
    }

    @Builder
    @Getter(AccessLevel.PUBLIC)
    @Accessors(chain = true, fluent = true)
    public static class PlacementSettings {
        @Builder.Default
        protected final long salt = 0x76694565C616765L;
        @Builder.Default
        protected final int minDistance = 0;
        @Builder.Default
        protected final int maxDistance = 1;
        @Builder.Default
        Function<Integer, Boolean> isBiomeValid = biomeId -> true;
    }

}
