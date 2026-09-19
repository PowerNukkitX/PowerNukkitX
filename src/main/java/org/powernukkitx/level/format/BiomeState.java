package org.powernukkitx.level.format;

import it.unimi.dsi.fastutil.ints.Int2ByteMap;
import it.unimi.dsi.fastutil.ints.Int2ByteOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeClimateData;
import org.powernukkitx.registry.Registries;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Persistent per-chunk biome snow-accumulation state.
 *
 * @author Curse
 */
public final class BiomeState {
    private static final float DEFAULT_SNOW_ACCUMULATION_MAX = 0.125f;
    private final Int2ByteOpenHashMap snowAccumulation;
    private final AtomicLong storageChanges = new AtomicLong();
    private final AtomicLong persistedStorageChanges = new AtomicLong();

    public BiomeState() {
        this.snowAccumulation = new Int2ByteOpenHashMap();
    }

    public BiomeState(Int2ByteMap snowAccumulation) {
        this.snowAccumulation = new Int2ByteOpenHashMap(snowAccumulation);
    }

    /**
     * Returns the biome snow-accumulation entries.
     */
    public Int2ByteMap snowAccumulation() {
        return snowAccumulation;
    }

    /**
     * Updates the tracked state for an assigned biome.
     */
    public void updateBiome(int biomeId) {
        if (isTrackedBiome(biomeId)) {
            snowAccumulation.putIfAbsent(biomeId, (byte) 0);
        } else {
            snowAccumulation.remove(biomeId);
        }
    }

    /**
     * Rebuilds the tracked biome set while preserving existing cached values.
     */
    public void retainTrackedBiomes(IntSet biomeIds) {
        var iterator = snowAccumulation.keySet().iterator();
        while (iterator.hasNext()) {
            if (!biomeIds.contains(iterator.nextInt())) {
                iterator.remove();
            }
        }

        for (int biomeId : biomeIds) {
            snowAccumulation.putIfAbsent(biomeId, (byte) 0);
        }
    }

    /**
     * Marks the BiomeState persistence field as changed.
     */
    public void markStorageChanged() {
        storageChanges.incrementAndGet();
    }

    /**
     * Returns whether the persisted BiomeState field has changed.
     */
    public boolean hasStorageChanges() {
        return storageChanges.get() != persistedStorageChanges.get();
    }

    /**
     * Returns the current BiomeState storage change version.
     */
    public long getStorageChangeVersion() {
        return storageChanges.get();
    }

    /**
     * Marks a successfully persisted BiomeState change version.
     */
    public void markStorageSaved(long version) {
        persistedStorageChanges.accumulateAndGet(version, Math::max);
    }

    /**
     * Returns the effective runtime snow accumulation maximum.
     */
    public static float getEffectiveSnowAccumulationMax(BiomeClimateData climate) {
        float maximum = climate.getSnowAccumulationMax();
        return maximum > 0.0f ? maximum : DEFAULT_SNOW_ACCUMULATION_MAX;
    }

    /**
     * Returns whether tracks snow accumulation state for this biome.
     */
    public static boolean isTrackedBiome(int biomeId) {
        var registeredBiome = Registries.BIOME.get(biomeId);
        if (registeredBiome == null) {
            return false;
        }

        var definition = registeredBiome.second();
        var chunkGenData = definition.getChunkGenData();
        if (chunkGenData == null || chunkGenData.getClimate() == null) {
            return false;
        }

        return getEffectiveSnowAccumulationMax(chunkGenData.getClimate()) > 0.0f
                && definition.getTemperature() - 0.32f < 0.15f;
    }

    /**
     * Returns whether this state has no tracked biomes.
     */
    public boolean isEmpty() {
        return snowAccumulation.isEmpty();
    }
}