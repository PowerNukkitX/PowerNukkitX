package org.powernukkitx.level.generator.stages;

import org.powernukkitx.level.format.ChunkSection;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateFeature;
import org.powernukkitx.level.generator.GenerateStage;
import org.powernukkitx.level.generator.object.GeneratorRoot;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.BiomeTags;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeConsolidatedFeatureData;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionChunkGenData;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

@Slf4j
public class NormalChunkFeatureStage extends GenerateStage {

    public static final String NAME = "feature";
    protected static final String PREGENERATION_PASS = "pregeneration_pass";
    private static final int PREGENERATION_BIOME_CACHE_LIMIT = 8192;
    private static final Map<IChunk, IntOpenHashSet> PREGENERATION_BIOMES = new WeakHashMap<>();

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        final IntOpenHashSet allBiomesInChunk = this.resolveBiomesInChunk(chunk);

        Set<String> featureIdentifiers = new ObjectOpenHashSet<>();
        Int2ObjectOpenHashMap<BiomeConsolidatedFeatureData> featuresByIdentifier = new Int2ObjectOpenHashMap<>();
        for (int biomeId : allBiomesInChunk) {
            Pair<Short, BiomeDefinitionData> definition = Registries.BIOME.get(biomeId);
            BiomeDefinitionData biome = definition.second();
            BiomeDefinitionChunkGenData chunkGenData = biome.getChunkGenData();
            if (chunkGenData != null) {
                List<BiomeConsolidatedFeatureData> consolidatedFeaturesData = chunkGenData.getConsolidatedFeatures();
                if (consolidatedFeaturesData != null) {
                    for (BiomeConsolidatedFeatureData consolidatedFeatureData : consolidatedFeaturesData) {
                        if (!this.shouldApplyFeature(consolidatedFeatureData)) {
                            continue;
                        }
                        if (featureIdentifiers.add(Registries.BIOME.getFromBiomeStringList(consolidatedFeatureData.getIdentifier()))) {
                            featuresByIdentifier.put(consolidatedFeatureData.getIdentifier(), consolidatedFeatureData);
                        }
                    }
                }
            } else if (Registries.BIOME.containsTag(BiomeTags.JUNGLE, biome)) {
                log.warn("No chunkGenData for biome {}", definition.first());
            }
        }

        ArrayList<BiomeConsolidatedFeatureData> sortedFeatures = new ArrayList<>(featuresByIdentifier.values());
        sortedFeatures.sort(Comparator.comparingInt(feature -> feature.getScatter().getEvalOrder().ordinal()));

        GeneratorRoot root = new GeneratorRoot(chunk.getLevel());
        for (BiomeConsolidatedFeatureData consolidatedFeatureData : sortedFeatures) {
            String featureIdentifier = Registries.BIOME.getFromBiomeStringList(consolidatedFeatureData.getIdentifier()); //Usually more specific. Like contains biome and type.
            String featureName = Registries.BIOME.getFromBiomeStringList(consolidatedFeatureData.getFeature()); //Usually globally usable. But not always descriptive enough to use (e.g. ores)
            String selectedKey = null;
            if (Registries.GENERATE_FEATURE.has(featureIdentifier)) {
                selectedKey = featureIdentifier;
            } else if (Registries.GENERATE_FEATURE.has(featureName)) {
                selectedKey = featureName;
            }

            if (selectedKey != null) {
                try {
                    GenerateFeature feature = Registries.GENERATE_FEATURE.get(selectedKey);
                    feature.setRoot(root);
                    feature.apply(context);
                } catch (Exception e) {
                    log.error("Error while applying feature {}", selectedKey, e);
                }
            }
        }
        root.applySubChunkUpdate();
    }

    protected boolean cacheBiomesForPopulation() {
        return false;
    }

    static void cacheGeneratedBiomes(IChunk chunk, IntOpenHashSet biomes) {
        synchronized (PREGENERATION_BIOMES) {
            if (PREGENERATION_BIOMES.size() >= PREGENERATION_BIOME_CACHE_LIMIT && !PREGENERATION_BIOMES.containsKey(chunk)) {
                PREGENERATION_BIOMES.clear();
            }
            PREGENERATION_BIOMES.put(chunk, biomes);
        }
    }

    private IntOpenHashSet resolveBiomesInChunk(IChunk chunk) {
        if (this.cacheBiomesForPopulation()) {
            synchronized (PREGENERATION_BIOMES) {
                IntOpenHashSet cached = PREGENERATION_BIOMES.get(chunk);
                if (cached != null) {
                    return cached;
                }
            }

            IntOpenHashSet biomes = collectBiomesInChunk(chunk, chunk.getLevel().getMinHeight());
            cacheGeneratedBiomes(chunk, biomes);
            return biomes;
        }

        synchronized (PREGENERATION_BIOMES) {
            IntOpenHashSet cached = PREGENERATION_BIOMES.remove(chunk);
            if (cached != null) {
                return cached;
            }
        }
        return collectBiomesInChunk(chunk, chunk.getLevel().getMinHeight());
    }

    protected boolean shouldApplyFeature(BiomeConsolidatedFeatureData feature) {
        return !PREGENERATION_PASS.equals(Registries.BIOME.getFromBiomeStringList(feature.getPass()));
    }

    private static IntOpenHashSet collectBiomesInChunk(IChunk chunk, int minHeight) {
        IntOpenHashSet allBiomesInChunk = new IntOpenHashSet();
        final ChunkSection[] sections = chunk.getSections();
        final int minSectionY = chunk.getDimensionData().getMinSectionY();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int y = chunk.getHeightMap(x, z) - 1;
                int currentSectionY = Integer.MIN_VALUE;
                ChunkSection currentSection = null;
                int previousBiomeId = 0;
                boolean hasPreviousBiome = false;

                while (y > minHeight) {
                    int sectionY = y >> 4;
                    if (sectionY != currentSectionY) {
                        int sectionIndex = sectionY - minSectionY;
                        currentSection = sectionIndex >= 0 && sectionIndex < sections.length ? sections[sectionIndex] : null;
                        currentSectionY = sectionY;
                    }

                    if (currentSection == null) {
                        y = Math.max(minHeight, (sectionY << 4) - 1);
                        continue;
                    }

                    for (int yInSection = y & 0x0f; yInSection >= 0 && y > minHeight; yInSection--, y--) {
                        int biomeId = currentSection.getBiomeId(x, yInSection, z);
                        if (!hasPreviousBiome || biomeId != previousBiomeId) {
                            allBiomesInChunk.add(biomeId);
                            previousBiomeId = biomeId;
                            hasPreviousBiome = true;
                        }
                    }
                }
            }
        }

        return allBiomesInChunk;
    }

    @Override
    public String name() {
        return NAME;
    }
}
