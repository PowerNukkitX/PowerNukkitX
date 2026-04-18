package cn.nukkit.level.generator.stages;

import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeConsolidatedFeatureData;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionChunkGenData;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionData;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class NormalChunkFeatureStage extends GenerateStage {

    public static final String NAME = "feature";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        Set<String> featureIdentifiers = new ObjectOpenHashSet<>();
        Object2IntArrayMap<BiomeConsolidatedFeatureData> features = new Object2IntArrayMap<>();
        IntOpenHashSet biomes = new IntOpenHashSet();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                biomes.clear();
                int y = chunk.getHeightMap(x, z);
                for (int i = y; i > chunk.getLevel().getMinHeight(); i--) {
                    biomes.add(chunk.getSection(i >> 4).getBiomeId(x, i & 0x0f, z));
                }
                for (int biomeId : biomes) {
                    Pair<Short, BiomeDefinitionData> definition = Registries.BIOME.get(biomeId);
                    BiomeDefinitionData biome = definition.second();
                    BiomeDefinitionChunkGenData chunkGenData = biome.getChunkGenData();
                    if (chunkGenData != null) {
                        List<BiomeConsolidatedFeatureData> consolidatedFeaturesData = chunkGenData.getConsolidatedFeatures();
                        if (consolidatedFeaturesData != null) {
                            for (BiomeConsolidatedFeatureData consolidatedFeatureData : consolidatedFeaturesData) {
                                if (featureIdentifiers.add(Registries.BIOME.getFromBiomeStringList(consolidatedFeatureData.getIdentifier()))) {
                                    features.put(consolidatedFeatureData, consolidatedFeatureData.getScatter().getEvalOrder().ordinal());
                                }
                            }
                        }
                    } else {
                        if (biome.getTags().contains(BiomeTags.JUNGLE)) {
                            log.warn("No chunkGenData for biome {}", definition.first());
                        }
                    }
                }
            }
        }
        for (var entry : features.object2IntEntrySet().stream().sorted(Map.Entry.comparingByValue()).toList()) {
            var consolidatedFeatureData = entry.getKey();
            String featureIdentifier = Registries.BIOME.getFromBiomeStringList(consolidatedFeatureData.getIdentifier()); //Usually more specific. Like contains biome and type.
            String featureName = Registries.BIOME.getFromBiomeStringList(consolidatedFeatureData.getFeature()); //Usually globally usable. But not always descriptive enough to use (e.g. ores)
            for (String key : new String[]{featureIdentifier, featureName}) {
                if (Registries.GENERATE_FEATURE.has(key)) {
                    try {
                        GenerateFeature feature = Registries.GENERATE_FEATURE.get(key);
                        feature.apply(context);
                    } catch (Exception e) {
                        log.error("Error while applying feature {}", key, e);
                    }
                    break;
                }
            }
        }
        chunk.setChunkState(ChunkState.POPULATED);
    }

    @Override
    public String name() {
        return NAME;
    }
}
