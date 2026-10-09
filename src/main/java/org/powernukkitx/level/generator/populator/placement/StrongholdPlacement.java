package org.powernukkitx.level.generator.populator.placement;

import org.powernukkitx.level.generator.biome.BiomePicker;
import org.powernukkitx.level.generator.biome.OverworldBiomePicker;
import org.powernukkitx.math.ChunkVector2;
import org.powernukkitx.math.MathHelper;
import org.powernukkitx.utils.random.BedrockRandom;
import org.powernukkitx.utils.random.RandomSourceProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import com.google.common.base.Preconditions;

/**
 * Stronghold placement, including the three village-anchored initial strongholds and far-field cells.
 *
 * @author Curse
 */
public final class StrongholdPlacement extends StructurePlacement {

    private static final int FAR_FIELD_REGION_SIZE = 200;
    private static final int FAR_FIELD_OFFSET_BOUND = 100;
    private static final int FAR_FIELD_OFFSET_MIN = 50;
    private static final int FAR_FIELD_SALT = 0x05D534E7;
    private static final int FAR_FIELD_X_MULTIPLIER = 0x9BABEB51;
    private static final int FAR_FIELD_Z_MULTIPLIER = 0xBE68AEB9;
    private static final float FAR_FIELD_PROBABILITY = 0.25f;
    private static final int FAR_FIELD_CACHE_LIMIT = 4096;
    private static final long CACHE_MISS = Long.MIN_VALUE;
    private static final long NO_CANDIDATE = Long.MAX_VALUE;

    private static final int INITIAL_COUNT = 3;
    private static final int INITIAL_RADIUS_MIN = 40;
    private static final int INITIAL_RADIUS_BOUND = 16;
    private static final int INITIAL_SEARCH_RADIUS = 8;
    private static final int INITIAL_MAX_ATTEMPTS = 1000;
    private static final float TWO_PI = 6.2831855f;
    private static final float INITIAL_SUCCESS_ANGLE_STEP = 1.8849558f;
    private static final float INITIAL_FAILURE_ANGLE_STEP = 0.7853982f;

    private static final ThreadLocal<BedrockRandom> RANDOMS = ThreadLocal.withInitial(() -> new BedrockRandom(0));

    private final StructureRandomSpreadPlacement villagePlacement;
    private final Map<BiomePicker<?>, InitialStrongholdCache> initialStrongholds = new WeakHashMap<>();
    private final ThreadLocal<Int2LongOpenHashMap> farFieldCandidates = ThreadLocal.withInitial(() -> {
        Int2LongOpenHashMap cache = new Int2LongOpenHashMap();
        cache.defaultReturnValue(CACHE_MISS);
        return cache;
    });

    /**
     * Creates stronghold placement using the village predicate used to anchor the first three strongholds.
     */
    public StrongholdPlacement(StructureRandomSpreadPlacement villagePlacement) {
        super(PlacementSettings.builder().salt(FAR_FIELD_SALT).maxDistance(FAR_FIELD_REGION_SIZE).build());
        this.villagePlacement = Preconditions.checkNotNull(villagePlacement, "villagePlacement");
    }

    @Override
    public boolean canGenerate(long levelSeed, RandomSourceProvider random, int chunkX, int chunkZ, int biome) {
        return this.isFarFieldStart(levelSeed, chunkX, chunkZ);
    }

    @Override
    public boolean canGenerate(long levelSeed, RandomSourceProvider random, int chunkX, int chunkZ, BiomePicker<?> biomePicker) {
        if (biomePicker == null) {
            return false;
        }

        for (ChunkVector2 initial : this.getInitialStrongholds(levelSeed, biomePicker)) {
            if (initial.getX() == chunkX && initial.getZ() == chunkZ) {
                return true;
            }
        }
        return this.isFarFieldStart(levelSeed, chunkX, chunkZ);
    }

    @Override
    public ChunkVector2 findNearestGenerationChunk(ChunkVector2 origin, RandomSourceProvider random, BiomePicker<?> biomePicker,
                                                    int radius) {
        if (origin == null || random == null || biomePicker == null || radius < 0) {
            return null;
        }

        long levelSeed = random.getSeed();
        int originX = origin.getX();
        int originZ = origin.getZ();
        int minChunkX = originX - radius;
        int maxChunkX = originX + radius;
        int minChunkZ = originZ - radius;
        int maxChunkZ = originZ + radius;
        ChunkVector2 nearest = null;
        long nearestDistanceSq = Long.MAX_VALUE;

        for (ChunkVector2 initial : this.getInitialStrongholds(levelSeed, biomePicker)) {
            int chunkX = initial.getX();
            int chunkZ = initial.getZ();
            if (chunkX < minChunkX || chunkX > maxChunkX || chunkZ < minChunkZ || chunkZ > maxChunkZ) {
                continue;
            }

            long distanceSq = distanceSquared(originX, originZ, chunkX, chunkZ);
            if (isNearer(nearest, nearestDistanceSq, distanceSq, chunkX, chunkZ)) {
                nearest = initial;
                nearestDistanceSq = distanceSq;
            }
        }

        int minRegionX = farFieldRegionCoord(minChunkX);
        int maxRegionX = farFieldRegionCoord(maxChunkX);
        int minRegionZ = farFieldRegionCoord(minChunkZ);
        int maxRegionZ = farFieldRegionCoord(maxChunkZ);

        for (int regionX = minRegionX; regionX <= maxRegionX; regionX++) {
            for (int regionZ = minRegionZ; regionZ <= maxRegionZ; regionZ++) {
                ChunkVector2 candidate = this.getFarFieldCandidate(levelSeed, regionX, regionZ);
                if (candidate == null) {
                    continue;
                }

                int chunkX = candidate.getX();
                int chunkZ = candidate.getZ();
                if (chunkX < minChunkX || chunkX > maxChunkX || chunkZ < minChunkZ || chunkZ > maxChunkZ
                        || isInsideOriginExclusion(chunkX, chunkZ)) {
                    continue;
                }

                long distanceSq = distanceSquared(originX, originZ, chunkX, chunkZ);
                if (isNearer(nearest, nearestDistanceSq, distanceSq, chunkX, chunkZ)) {
                    nearest = candidate;
                    nearestDistanceSq = distanceSq;
                }
            }
        }

        return nearest;
    }

    private boolean isFarFieldStart(long levelSeed, int chunkX, int chunkZ) {
        if (isInsideOriginExclusion(chunkX, chunkZ)) {
            return false;
        }

        int regionX = farFieldRegionCoord(chunkX);
        int regionZ = farFieldRegionCoord(chunkZ);
        long offsets = this.getFarFieldCandidateOffsets(levelSeed, regionX, regionZ);
        return offsets != NO_CANDIDATE
                && regionX * FAR_FIELD_REGION_SIZE + (int) (offsets >> 32) == chunkX
                && regionZ * FAR_FIELD_REGION_SIZE + (int) offsets == chunkZ;
    }

    private ChunkVector2 getFarFieldCandidate(long levelSeed, int regionX, int regionZ) {
        long offsets = this.getFarFieldCandidateOffsets(levelSeed, regionX, regionZ);
        if (offsets == NO_CANDIDATE) {
            return null;
        }

        int chunkX = regionX * FAR_FIELD_REGION_SIZE + (int) (offsets >> 32);
        int chunkZ = regionZ * FAR_FIELD_REGION_SIZE + (int) offsets;
        return new ChunkVector2(chunkX, chunkZ);
    }

    private long getFarFieldCandidateOffsets(long levelSeed, int regionX, int regionZ) {
        int centerX = regionX * FAR_FIELD_REGION_SIZE + FAR_FIELD_REGION_SIZE / 2;
        int centerZ = regionZ * FAR_FIELD_REGION_SIZE + FAR_FIELD_REGION_SIZE / 2;
        if (isInsideOriginExclusion(centerX, centerZ)) {
            return NO_CANDIDATE;
        }

        int seed = centerX * FAR_FIELD_X_MULTIPLIER + centerZ * FAR_FIELD_Z_MULTIPLIER + (int) levelSeed + FAR_FIELD_SALT;
        Int2LongOpenHashMap cache = this.farFieldCandidates.get();
        long cached = cache.get(seed);
        if (cached != CACHE_MISS) {
            return cached;
        }

        BedrockRandom random = RANDOMS.get().setSeed(seed);
        int offsetX = random.nextBoundedInt(FAR_FIELD_OFFSET_BOUND) + FAR_FIELD_OFFSET_MIN;
        int offsetZ = random.nextBoundedInt(FAR_FIELD_OFFSET_BOUND) + FAR_FIELD_OFFSET_MIN;
        long candidate = random.nextFloat() < FAR_FIELD_PROBABILITY
                ? ((long) offsetX << 32) | Integer.toUnsignedLong(offsetZ)
                : NO_CANDIDATE;

        if (cache.size() >= FAR_FIELD_CACHE_LIMIT) {
            cache.clear();
        }
        cache.put(seed, candidate);
        return candidate;
    }

    private List<ChunkVector2> getInitialStrongholds(long levelSeed, BiomePicker<?> biomePicker) {
        synchronized (this.initialStrongholds) {
            InitialStrongholdCache cached = this.initialStrongholds.get(biomePicker);
            if (cached != null && cached.levelSeed == levelSeed) {
                return cached.positions;
            }

            List<ChunkVector2> positions = List.copyOf(this.createInitialStrongholds(levelSeed, biomePicker));
            this.initialStrongholds.put(biomePicker, new InitialStrongholdCache(levelSeed, positions));
            return positions;
        }
    }

    private List<ChunkVector2> createInitialStrongholds(long levelSeed, BiomePicker<?> biomePicker) {
        BedrockRandom random = new BedrockRandom(levelSeed);
        float angle = random.nextFloat() * TWO_PI;
        int radius = random.nextBoundedInt(INITIAL_RADIUS_BOUND) + INITIAL_RADIUS_MIN;
        List<ChunkVector2> positions = new ArrayList<>(INITIAL_COUNT);

        for (int attempt = 0; positions.size() < INITIAL_COUNT && attempt < INITIAL_MAX_ATTEMPTS; attempt++) {
            int targetX = MathHelper.floor_float_int(radius * MathHelper.cos(angle));
            int targetZ = MathHelper.floor_float_int(radius * MathHelper.sin(angle));
            ChunkVector2 village = this.findVillageStart(levelSeed, biomePicker, targetX, targetZ);
            if (village != null) {
                positions.add(village);
                radius += 8;
                angle += INITIAL_SUCCESS_ANGLE_STEP;
            } else {
                radius += 4;
                angle += INITIAL_FAILURE_ANGLE_STEP;
            }
        }
        return positions;
    }

    private ChunkVector2 findVillageStart(long levelSeed, BiomePicker<?> biomePicker, int targetX, int targetZ) {
        for (int chunkX = targetX - INITIAL_SEARCH_RADIUS; chunkX < targetX + INITIAL_SEARCH_RADIUS; chunkX++) {
            for (int chunkZ = targetZ - INITIAL_SEARCH_RADIUS; chunkZ < targetZ + INITIAL_SEARCH_RADIUS; chunkZ++) {
                if (!this.villagePlacement.isCandidateChunk(levelSeed, chunkX, chunkZ)) {
                    continue;
                }
                int biome = this.sampleVillageBiome(biomePicker, chunkX, chunkZ);
                if (this.villagePlacement.isValidBiome(biome)) {
                    return new ChunkVector2(chunkX, chunkZ);
                }
            }
        }
        return null;
    }

    private int sampleVillageBiome(BiomePicker<?> biomePicker, int chunkX, int chunkZ) {
        int sampleX = (chunkX << 4) + 8;
        int sampleZ = (chunkZ << 4) + 8;
        int sampleY = this.villagePlacement.settings.biomeSampleY();
        if (biomePicker instanceof OverworldBiomePicker overworldBiomePicker) {
            return overworldBiomePicker.pickRaw(sampleX, sampleY, sampleZ).getBiomeId();
        }
        return biomePicker.pick(sampleX, sampleY, sampleZ).getBiomeId();
    }

    private static int farFieldRegionCoord(int chunk) {
        return MathHelper.floor_float_int((float) chunk / FAR_FIELD_REGION_SIZE);
    }

    private static boolean isInsideOriginExclusion(int chunkX, int chunkZ) {
        int distanceSq = chunkX * chunkX + chunkZ * chunkZ;
        return Integer.compareUnsigned(distanceSq, 100) < 0;
    }

    private static long distanceSquared(int x1, int z1, int x2, int z2) {
        long dx = (long) x2 - x1;
        long dz = (long) z2 - z1;
        return dx * dx + dz * dz;
    }

    private static boolean isNearer(ChunkVector2 nearest, long nearestDistanceSq, long distanceSq, int chunkX, int chunkZ) {
        return distanceSq < nearestDistanceSq
                || (distanceSq == nearestDistanceSq && (nearest == null
                || chunkX < nearest.getX()
                || (chunkX == nearest.getX() && chunkZ < nearest.getZ())));
    }

    private static final class InitialStrongholdCache {
        private final long levelSeed;
        private final List<ChunkVector2> positions;

        private InitialStrongholdCache(long levelSeed, List<ChunkVector2> positions) {
            this.levelSeed = levelSeed;
            this.positions = positions;
        }
    }
}
