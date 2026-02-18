package cn.nukkit.level.generator.stages;

import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.level.generator.GenerateStage;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeConsolidatedFeatureData;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionData;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionChunkGenData;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
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
                BiomeDefinitionData definition = Registries.BIOME.get(chunk.getBiomeId(x, y, z));
                BiomeDefinitionChunkGenData chunkGenData = definition.getChunkGenData();
                if(chunkGenData != null && chunkGenData.getConsolidatedFeatures() != null) {
                    for(BiomeConsolidatedFeatureData consolidatedFeatureData : chunkGenData.getConsolidatedFeatures()) {
                        features.put(consolidatedFeatureData, consolidatedFeatureData.getScatter().getEvalOrder().ordinal());
                    }
                } else {
                    if(definition.getTags().contains(BiomeTags.JUNGLE)) {
                        log.warn("No chunkGenData for biome {}", definition.getId());
                    }
                }
            }
        }
        for(var entry : features.object2IntEntrySet().stream().sorted(Map.Entry.comparingByValue()).toList()) {
            var consolidatedFeatureData = entry.getKey();
            String featureIdentifier = consolidatedFeatureData.getIdentifier();
            String featureName = consolidatedFeatureData.getFeature();
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
