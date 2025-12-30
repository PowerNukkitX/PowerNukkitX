package cn.nukkit.level.generator.stages;

import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.network.protocol.types.biome.BiomeConsolidatedFeatureData;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.network.protocol.types.biome.BiomeDefinitionChunkGenData;
import cn.nukkit.network.protocol.types.biome.BiomeDefinitionData;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.OptionalValue;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class NormalChunkFeatureStage extends GenerateStage {

    public static final String NAME = "feature";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        Object2IntArrayMap<BiomeConsolidatedFeatureData> features = new Object2IntArrayMap<>();
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                int y = chunk.getHeightMap(x, z);
                BiomeDefinition definition = Registries.BIOME.get(chunk.getBiomeId(x, y, z));
                BiomeDefinitionData biome = definition.data;
                OptionalValue<BiomeDefinitionChunkGenData> chunkGenDataOptional = biome.chunkGenData;
                if(chunkGenDataOptional.isPresent()) {
                    BiomeDefinitionChunkGenData chunkGenData = chunkGenDataOptional.get();
                    OptionalValue<BiomeConsolidatedFeatureData[]> consolidatedFeatureDataOptional = chunkGenData.consolidatedFeatures;
                    if(consolidatedFeatureDataOptional.isPresent()) {
                        BiomeConsolidatedFeatureData[] consolidatedFeatureDataArray = consolidatedFeatureDataOptional.get();
                        for(BiomeConsolidatedFeatureData consolidatedFeatureData : consolidatedFeatureDataArray) {
                            features.put(consolidatedFeatureData, consolidatedFeatureData.scatter.evalOrder);
                        }
                    }
                } else {
                    if(definition.getTags().contains(BiomeTags.JUNGLE)) {
                        log.warn("No chunkGenData for biome {}", definition.getName());
                    }
                }
            }
        }
        for(var entry : features.object2IntEntrySet().stream().sorted(Map.Entry.comparingByValue()).toList()) {
            var consolidatedFeatureData = entry.getKey();
            String featureIdentifier = Registries.BIOME.getFromBiomeStringList(consolidatedFeatureData.identifier); //Usually more specific. Like contains biome and type.
            String featureName = Registries.BIOME.getFromBiomeStringList(consolidatedFeatureData.feature); //Usually globally usable. But not always descriptive enough to use (e.g. ores)
            for(String key : new String[]{featureIdentifier, featureName}) {
                if(Registries.GENERATE_FEATURE.has(key)) {
                    try {
                        GenerateFeature feature = Registries.GENERATE_FEATURE.get(key);
                        feature.apply(context);
                    } catch (Exception e) {
                        log.error("Error while applying feature {}", key, e);
                    }
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
