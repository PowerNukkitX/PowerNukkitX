package cn.nukkit.level.generator.stages.normal;

import cn.nukkit.block.BlockIce;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockWater;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.network.protocol.types.biome.BiomeConsolidatedFeatureData;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.network.protocol.types.biome.BiomeDefinitionChunkGenData;
import cn.nukkit.network.protocol.types.biome.BiomeDefinitionData;
import cn.nukkit.network.protocol.types.biome.BiomeSurfaceMaterialData;
import cn.nukkit.registry.Registries;
import cn.nukkit.tags.BiomeTags;
import cn.nukkit.utils.OptionalValue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NormalChunkFeatureStage extends GenerateStage {

    public static final String NAME = "normal_feature";


    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
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
                            String featureName = Registries.BIOME.getFromBiomeStringList(consolidatedFeatureData.feature);
                            if(Registries.GENERATE_FEATURE.has(featureName)) {
                                GenerateFeature feature = Registries.GENERATE_FEATURE.get(featureName);
                                if(feature != null) {
                                    feature.apply(context);
                                }
                            } else {
                                if(definition.getTags().contains(BiomeTags.JUNGLE)) {
                                    //System.out.println(featureName);
                                }
                            }
                        }
                    }
                } else {
                    log.warn("No chunkGenData for biome " + definition.getName());
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
