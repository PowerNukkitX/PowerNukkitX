package cn.nukkit.level.generator.stages;

import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.network.protocol.types.biome.BiomeConsolidatedFeatureData;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.network.protocol.types.biome.BiomeDefinitionChunkGenData;
import cn.nukkit.network.protocol.types.biome.BiomeDefinitionData;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.OptionalValue;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;

@Slf4j
public class NormalChunkFeatureStage extends GenerateStage {

    public static final String NAME = "feature";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        final int minHeight = chunk.getLevel().getMinHeight();
        final IntOpenHashSet allBiomesInChunk = collectBiomesInChunk(chunk, minHeight);

        Int2ObjectOpenHashMap<BiomeConsolidatedFeatureData> featuresByIdentifier = new Int2ObjectOpenHashMap<>();
        for (int biomeId : allBiomesInChunk) {
            BiomeDefinition definition = Registries.BIOME.get(biomeId);
            BiomeDefinitionData biome = definition.data;
            OptionalValue<BiomeDefinitionChunkGenData> chunkGenDataOptional = biome.chunkGenData;
            if (chunkGenDataOptional.isPresent()) {
                BiomeDefinitionChunkGenData chunkGenData = chunkGenDataOptional.get();
                OptionalValue<BiomeConsolidatedFeatureData[]> consolidatedFeatureDataOptional = chunkGenData.consolidatedFeatures;
                if (consolidatedFeatureDataOptional.isPresent()) {
                    BiomeConsolidatedFeatureData[] consolidatedFeatureDataArray = consolidatedFeatureDataOptional.get();
                    for (BiomeConsolidatedFeatureData consolidatedFeatureData : consolidatedFeatureDataArray) {
                        featuresByIdentifier.putIfAbsent(consolidatedFeatureData.identifier, consolidatedFeatureData);
                    }
                }
            } else if (definition.getTags().contains(BiomeTags.JUNGLE)) {
                log.warn("No chunkGenData for biome {}", definition.getName());
            }
        }

        ArrayList<BiomeConsolidatedFeatureData> sortedFeatures = new ArrayList<>(featuresByIdentifier.values());
        sortedFeatures.sort(Comparator.comparingInt(feature -> feature.scatter.evalOrder));

        BlockManager root = new BlockManager(chunk.getLevel());
        for (BiomeConsolidatedFeatureData consolidatedFeatureData : sortedFeatures) {
            String featureIdentifier = Registries.BIOME.getFromBiomeStringList(consolidatedFeatureData.identifier); //Usually more specific. Like contains biome and type.
            String featureName = Registries.BIOME.getFromBiomeStringList(consolidatedFeatureData.feature); //Usually globally usable. But not always descriptive enough to use (e.g. ores)
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
        chunk.setChunkState(ChunkState.POPULATED);
    }

    private static IntOpenHashSet collectBiomesInChunk(IChunk chunk, int minHeight) {
        IntOpenHashSet allBiomesInChunk = new IntOpenHashSet();
        final ChunkSection[] sections = chunk.getSections();
        final int minSectionY = chunk.getDimensionData().getMinSectionY();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int y = chunk.getHeightMap(x, z);
                int currentSectionY = Integer.MIN_VALUE;
                ChunkSection currentSection = null;

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
                        allBiomesInChunk.add(currentSection.getBiomeId(x, yInSection, z));
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
