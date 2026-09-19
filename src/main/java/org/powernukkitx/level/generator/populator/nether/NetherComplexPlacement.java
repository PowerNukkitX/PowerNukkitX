package org.powernukkitx.level.generator.populator.nether;

import org.powernukkitx.level.biome.BiomeID;
import org.powernukkitx.level.generator.biome.BiomePicker;
import org.powernukkitx.math.ChunkVector2;
import org.powernukkitx.utils.random.BedrockRandom;
import org.powernukkitx.utils.random.RandomSourceProvider;

public final class NetherComplexPlacement {

    public static final int REGION_SIZE_CHUNKS = 30;
    public static final int EDGE_EXCLUSION_CHUNKS = 4;
    static final int PLACEMENT_SALT = 0x01CB0C88;
    private static final int CANDIDATE_OFFSET_BOUND = REGION_SIZE_CHUNKS - EDGE_EXCLUSION_CHUNKS;
    private static final int SELECTOR_BOUND = 6;
    private static final int FORTRESS_SELECTOR_COUNT = 2;
    private static final int REGION_X_MULTIPLIER = 0x9939F508;
    private static final int REGION_Z_MULTIPLIER = 0xF1565BD5;
    private static final ThreadLocal<BedrockRandom> RANDOMS = ThreadLocal.withInitial(() -> new BedrockRandom(0));

    private NetherComplexPlacement() {
    }

    static boolean isNetherComplexStart(long levelSeed, int chunkX, int chunkZ) {
        int regionX = Math.floorDiv(chunkX, REGION_SIZE_CHUNKS);
        int regionZ = Math.floorDiv(chunkZ, REGION_SIZE_CHUNKS);
        BedrockRandom random = RANDOMS.get().setSeed(getRegionSeed(levelSeed, regionX, regionZ));
        int candidateX = regionX * REGION_SIZE_CHUNKS + random.nextBoundedInt(CANDIDATE_OFFSET_BOUND);
        int candidateZ = regionZ * REGION_SIZE_CHUNKS + random.nextBoundedInt(CANDIDATE_OFFSET_BOUND);
        return candidateX == chunkX && candidateZ == chunkZ;
    }

    static boolean shouldGenerateBastion(long levelSeed, int chunkX, int chunkZ) {
        BedrockRandom random = createPostSpreadRandom(RANDOMS.get(), levelSeed, chunkX, chunkZ);
        return random.nextBoundedInt(SELECTOR_BOUND) >= FORTRESS_SELECTOR_COUNT;
    }

    static BedrockRandom createPostSelectionRandom(long levelSeed, int chunkX, int chunkZ) {
        BedrockRandom random = createPostSpreadRandom(new BedrockRandom(0), levelSeed, chunkX, chunkZ);
        random.nextBoundedInt(SELECTOR_BOUND);
        return random;
    }

    static ChunkVector2 findNearestGenerationChunk(ChunkVector2 origin, RandomSourceProvider random, BiomePicker<?> biomePicker,
                                                     int radius, boolean bastion) {
        if (origin == null || random == null || radius < 0 || (bastion && biomePicker == null)) {
            return null;
        }

        long levelSeed = random.getSeed();
        int originX = origin.getX();
        int originZ = origin.getZ();
        int minChunkX = originX - radius;
        int maxChunkX = originX + radius;
        int minChunkZ = originZ - radius;
        int maxChunkZ = originZ + radius;

        int minRegionX = Math.floorDiv(minChunkX, REGION_SIZE_CHUNKS);
        int maxRegionX = Math.floorDiv(maxChunkX, REGION_SIZE_CHUNKS);
        int minRegionZ = Math.floorDiv(minChunkZ, REGION_SIZE_CHUNKS);
        int maxRegionZ = Math.floorDiv(maxChunkZ, REGION_SIZE_CHUNKS);

        BedrockRandom bedrockRandom = RANDOMS.get();
        ChunkVector2 nearest = null;
        long nearestDistanceSq = Long.MAX_VALUE;
        for (int regionX = minRegionX; regionX <= maxRegionX; regionX++) {
            for (int regionZ = minRegionZ; regionZ <= maxRegionZ; regionZ++) {
                bedrockRandom.setSeed(getRegionSeed(levelSeed, regionX, regionZ));
                int chunkX = regionX * REGION_SIZE_CHUNKS + bedrockRandom.nextBoundedInt(CANDIDATE_OFFSET_BOUND);
                int chunkZ = regionZ * REGION_SIZE_CHUNKS + bedrockRandom.nextBoundedInt(CANDIDATE_OFFSET_BOUND);
                if (chunkX < minChunkX || chunkX > maxChunkX || chunkZ < minChunkZ || chunkZ > maxChunkZ) {
                    continue;
                }

                boolean selectedBastion = bedrockRandom.nextBoundedInt(SELECTOR_BOUND) >= FORTRESS_SELECTOR_COUNT;
                if (selectedBastion != bastion) {
                    continue;
                }

                if (bastion) {
                    int biome = biomePicker.pick((chunkX << 4) + 7, 33, (chunkZ << 4) + 7).getBiomeId();
                    if (biome == BiomeID.BASALT_DELTAS) {
                        continue;
                    }
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
        return nearest;
    }

    private static BedrockRandom createPostSpreadRandom(BedrockRandom random, long levelSeed, int chunkX, int chunkZ) {
        int regionX = Math.floorDiv(chunkX, REGION_SIZE_CHUNKS);
        int regionZ = Math.floorDiv(chunkZ, REGION_SIZE_CHUNKS);
        random.setSeed(getRegionSeed(levelSeed, regionX, regionZ));
        random.nextBoundedInt(CANDIDATE_OFFSET_BOUND);
        random.nextBoundedInt(CANDIDATE_OFFSET_BOUND);
        return random;
    }

    private static int getRegionSeed(long levelSeed, int regionX, int regionZ) {
        return regionX * REGION_X_MULTIPLIER + regionZ * REGION_Z_MULTIPLIER + (int) levelSeed + PLACEMENT_SALT;
    }
}
