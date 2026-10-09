package org.powernukkitx.level.format.leveldb;

import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
import org.powernukkitx.level.format.BiomeState;
import org.powernukkitx.registry.Registries;

import java.util.ArrayList;
import java.util.List;

/**
 * Mutable world-level biome snow and foliage accumulation state.
 *
 * @author Curse
 */
final class WorldBiomeSnowState {
    private final int[] biomeIds;
    private final Int2FloatOpenHashMap snowAccumulation = new Int2FloatOpenHashMap();
    private final Int2FloatOpenHashMap foliageSnow = new Int2FloatOpenHashMap();

    WorldBiomeSnowState(NbtMap biomeData) {
        this.biomeIds = Registries.BIOME.getBiomeDefinitions().stream()
                .mapToInt(definition -> Registries.BIOME.getBiomeId(Registries.BIOME.getFromBiomeStringList(definition.first())))
                .sorted()
                .toArray();

        initializeDefaults();
        load(biomeData);
    }

    synchronized float getSnowAccumulation(int biomeId) {
        return snowAccumulation.get(biomeId);
    }

    synchronized void tick(float previousRainLevel, float currentRainLevel) {
        for (int biomeId : biomeIds) {
            var registeredBiome = Registries.BIOME.get(biomeId);
            if (registeredBiome == null) continue;

            var definition = registeredBiome.second();
            var chunkGenData = definition.getChunkGenData();
            if (chunkGenData == null || chunkGenData.getClimate() == null) continue;

            var climate = chunkGenData.getClimate();
            float temperature = definition.getTemperature();
            float downfall = definition.getDownfall();
            float snow = snowAccumulation.get(biomeId);
            float foliage = foliageSnow.get(biomeId);

            if (currentRainLevel > 0.2f) {
                if (temperature <= 0.15f) {
                    snow += previousRainLevel * downfall * 0.001f / 20.0f;
                    foliage += previousRainLevel * downfall * 0.04f / 20.0f;
                } else {
                    float temperatureDelta = 0.15f - temperature;
                    snow += previousRainLevel * downfall * temperatureDelta * 0.002f / 20.0f;
                    foliage += previousRainLevel * downfall * temperatureDelta * 0.08f / 20.0f;
                }
            } else {
                snow -= 0.00005f;
                foliage -= 0.002f;
            }

            snowAccumulation.put(
                    biomeId,
                    Math.max(climate.getSnowAccumulationMin(), Math.min(BiomeState.getEffectiveSnowAccumulationMax(climate), snow))
            );
            foliageSnow.put(biomeId, Math.max(0.0f, Math.min(1.0f, foliage)));
        }
    }

    synchronized NbtMap createNbt() {
        List<NbtMap> entries = new ArrayList<>();

        for (int biomeId : biomeIds) {
            float snow = snowAccumulation.get(biomeId);
            float foliage = foliageSnow.get(biomeId);
            if (snow <= 0.0f && foliage <= 0.0f) continue;

            NbtMapBuilder entry = NbtMap.builder().putShort("id", (short) biomeId);
            if (snow > 0.0f) {
                entry.putFloat("snowAccumulation", snow);
            }
            if (foliage > 0.0f) {
                entry.putFloat("foliageSnow", foliage);
            }
            entries.add(entry.build());
        }

        return entries.isEmpty() ? null : NbtMap.builder().putList("list", NbtType.COMPOUND, entries).build();
    }

    private void initializeDefaults() {
        snowAccumulation.clear();
        foliageSnow.clear();

        for (int biomeId : biomeIds) {
            var registeredBiome = Registries.BIOME.get(biomeId);
            if (registeredBiome == null) continue;

            var definition = registeredBiome.second();
            var chunkGenData = definition.getChunkGenData();
            float minimum = chunkGenData == null || chunkGenData.getClimate() == null
                    ? 0.0f
                    : chunkGenData.getClimate().getSnowAccumulationMin();

            snowAccumulation.put(biomeId, minimum);
            foliageSnow.put(biomeId, definition.getFoliageSnow());
        }
    }

    private void load(NbtMap biomeData) {
        Object rawList = biomeData.get("list");
        if (!(rawList instanceof List<?> list)) return;

        for (Object rawEntry : list) {
            if (!(rawEntry instanceof NbtMap entry)) continue;

            Object rawId = entry.get("id");
            if (!(rawId instanceof Number id)) continue;

            int biomeId = id.intValue() & 0xffff;
            if (Registries.BIOME.get(biomeId) == null) continue;

            Object rawSnowAccumulation = entry.get("snowAccumulation");
            if (rawSnowAccumulation instanceof Number value) {
                snowAccumulation.put(biomeId, value.floatValue());
            }

            Object rawFoliageSnow = entry.get("foliageSnow");
            if (rawFoliageSnow instanceof Number value) {
                foliageSnow.put(biomeId, value.floatValue());
            }
        }
    }
}
