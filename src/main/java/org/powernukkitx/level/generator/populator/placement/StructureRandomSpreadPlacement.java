package org.powernukkitx.level.generator.populator.placement;

import org.powernukkitx.level.generator.biome.BiomePicker;
import org.powernukkitx.level.generator.biome.OverworldBiomePicker;
import org.powernukkitx.math.ChunkVector2;
import org.powernukkitx.utils.random.BedrockRandom;
import org.powernukkitx.utils.random.RandomSourceProvider;

import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import com.google.common.base.Preconditions;

/**
 * Random-spread structure candidate placement using 32-bit region seeding and MT19937 random source.
 *
 * @author Curse
 */
public class StructureRandomSpreadPlacement extends StructurePlacement {

    private static final int REGION_X_MULTIPLIER = 0x9939F508;
    private static final int REGION_Z_MULTIPLIER = 0xF1565BD5;
    private static final int CANDIDATE_CACHE_LIMIT = 4096;
    private static final long CACHE_MISS = Long.MIN_VALUE;
    private static final ThreadLocal<BedrockRandom> RANDOMS = ThreadLocal.withInitial(() -> new BedrockRandom(0));

    private final SpreadType spreadType;
    private final ThreadLocal<Int2LongOpenHashMap> candidateOffsets = ThreadLocal.withInitial(() -> {
        Int2LongOpenHashMap cache = new Int2LongOpenHashMap();
        cache.defaultReturnValue(CACHE_MISS);
        return cache;
    });

    /**
     * Creates a random-spread placement.
     */
    public StructureRandomSpreadPlacement(StructurePlacement.PlacementSettings settings, SpreadType spreadType) {
        super(Preconditions.checkNotNull(settings, "settings"));
        this.spreadType = Preconditions.checkNotNull(spreadType, "spreadType");
        Preconditions.checkArgument(this.settings.minDistance() >= 0, "separation must be non-negative");
        Preconditions.checkArgument(this.settings.maxDistance() > this.settings.minDistance(), "spacing must be greater than separation");
    }

    /**
     * Creates the random state immediately after this region's spread offsets have been sampled.
     */
    public BedrockRandom createPostSpreadRandom(long levelSeed, int chunkX, int chunkZ) {
        int spacing = this.settings.maxDistance();
        int regionX = Math.floorDiv(chunkX, spacing);
        int regionZ = Math.floorDiv(chunkZ, spacing);
        BedrockRandom random = new BedrockRandom(this.getRegionSeed(levelSeed, regionX, regionZ));
        int range = spacing - this.settings.minDistance();
        this.nextOffset(random, range);
        this.nextOffset(random, range);
        return random;
    }

    /**
     * Returns whether this chunk is the deterministic spread candidate for its region.
     */
    public boolean isCandidateChunk(long levelSeed, int chunkX, int chunkZ) {
        int spacing = this.settings.maxDistance();
        int regionX = Math.floorDiv(chunkX, spacing);
        int regionZ = Math.floorDiv(chunkZ, spacing);
        int range = spacing - this.settings.minDistance();
        long offsets = this.getCandidateOffsets(this.getRegionSeed(levelSeed, regionX, regionZ), range);
        int candidateX = regionX * spacing + (int) (offsets >> 32);
        int candidateZ = regionZ * spacing + (int) offsets;
        return candidateX == chunkX && candidateZ == chunkZ;
    }

    @Override
    public boolean canGenerate(long levelSeed, RandomSourceProvider random, int chunkX, int chunkZ, int biome) {
        return this.isCandidateChunk(levelSeed, chunkX, chunkZ) && this.isValidBiome(biome);
    }

    @Override
    public boolean canGenerate(long levelSeed, RandomSourceProvider random, int chunkX, int chunkZ, BiomePicker<?> biomePicker) {
        return this.isCandidateChunk(levelSeed, chunkX, chunkZ) && this.isValidBiome(biomePicker, chunkX, chunkZ);
    }

    @Override
    public ChunkVector2 findNearestGenerationChunk(ChunkVector2 origin, RandomSourceProvider random, BiomePicker<?> biomePicker, int radius) {
        if (origin == null || random == null || biomePicker == null || radius < 0) {
            return null;
        }

        long levelSeed = random.getSeed();
        int spacing = this.settings.maxDistance();
        int range = spacing - this.settings.minDistance();
        int originX = origin.getX();
        int originZ = origin.getZ();
        int minChunkX = originX - radius;
        int maxChunkX = originX + radius;
        int minChunkZ = originZ - radius;
        int maxChunkZ = originZ + radius;
        int minRegionX = Math.floorDiv(minChunkX, spacing);
        int maxRegionX = Math.floorDiv(maxChunkX, spacing);
        int minRegionZ = Math.floorDiv(minChunkZ, spacing);
        int maxRegionZ = Math.floorDiv(maxChunkZ, spacing);

        ChunkVector2 nearest = null;
        long nearestDistanceSq = Long.MAX_VALUE;

        for (int regionX = minRegionX; regionX <= maxRegionX; regionX++) {
            int originRegionX = regionX * spacing;
            for (int regionZ = minRegionZ; regionZ <= maxRegionZ; regionZ++) {
                int originRegionZ = regionZ * spacing;
                long offsets = this.getCandidateOffsets(this.getRegionSeed(levelSeed, regionX, regionZ), range);
                int chunkX = originRegionX + (int) (offsets >> 32);
                int chunkZ = originRegionZ + (int) offsets;
                if (chunkX < minChunkX || chunkX > maxChunkX || chunkZ < minChunkZ || chunkZ > maxChunkZ) {
                    continue;
                }
                if (!this.isValidBiome(biomePicker, chunkX, chunkZ)) {
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

        return nearest;
    }

    protected boolean isValidBiome(BiomePicker<?> biomePicker, int chunkX, int chunkZ) {
        return this.isValidBiome(this.sampleBiome(biomePicker, chunkX, chunkZ));
    }

    private long getCandidateOffsets(int regionSeed, int range) {
        Int2LongOpenHashMap cache = this.candidateOffsets.get();
        long cached = cache.get(regionSeed);
        if (cached != CACHE_MISS) {
            return cached;
        }

        BedrockRandom placementRandom = RANDOMS.get().setSeed(regionSeed);
        int offsetX = this.nextOffset(placementRandom, range);
        int offsetZ = this.nextOffset(placementRandom, range);
        long offsets = ((long) offsetX << 32) | Integer.toUnsignedLong(offsetZ);

        if (cache.size() >= CANDIDATE_CACHE_LIMIT) {
            cache.clear();
        }
        cache.put(regionSeed, offsets);
        return offsets;
    }

    private int getRegionSeed(long levelSeed, int regionX, int regionZ) {
        return regionX * REGION_X_MULTIPLIER + regionZ * REGION_Z_MULTIPLIER + (int) levelSeed + (int) this.settings.salt();
    }

    private int nextOffset(BedrockRandom random, int range) {
        return switch (this.spreadType) {
            case LINEAR -> random.nextBoundedInt(range);
            case TRIANGULAR -> (random.nextBoundedInt(range) + random.nextBoundedInt(range)) / 2;
        };
    }

    private int sampleBiome(BiomePicker<?> biomePicker, int chunkX, int chunkZ) {
        int sampleOffset = this.settings.biomeSampleOffset();
        int sampleX = (chunkX << 4) + sampleOffset;
        int sampleZ = (chunkZ << 4) + sampleOffset;
        int sampleY = this.settings.biomeSampleY();
        if (biomePicker instanceof OverworldBiomePicker overworldBiomePicker) {
            return overworldBiomePicker.pickRaw(sampleX, sampleY, sampleZ).getBiomeId();
        }
        return biomePicker.pick(sampleX, sampleY, sampleZ).getBiomeId();
    }

    /**
     * Spread distribution used by random-spread placements.
     */
    public enum SpreadType {
        LINEAR,
        TRIANGULAR
    }
}
