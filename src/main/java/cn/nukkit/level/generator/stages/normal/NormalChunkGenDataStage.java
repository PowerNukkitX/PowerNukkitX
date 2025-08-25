package cn.nukkit.level.generator.stages.normal;

import cn.nukkit.block.BlockIce;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockWater;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.network.protocol.types.biome.BiomeConsolidatedFeatureData;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.network.protocol.types.biome.BiomeDefinitionChunkGenData;
import cn.nukkit.network.protocol.types.biome.BiomeDefinitionData;
import cn.nukkit.network.protocol.types.biome.BiomeSurfaceMaterialData;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.OptionalValue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NormalChunkGenDataStage extends GenerateStage {

    public static final String NAME = "normal_chunkgendata";


    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                int y = chunk.getHeightMap(x, z);
                BlockState topBlockState = chunk.getBlockState(x, y, z);
                BiomeDefinition definition = Registries.BIOME.get(chunk.getBiomeId(x, y, z));
                BiomeDefinitionData biome = definition.data;
                OptionalValue<BiomeDefinitionChunkGenData> chunkGenDataOptional = biome.chunkGenData;
                if(chunkGenDataOptional.isPresent()) {
                    BiomeDefinitionChunkGenData chunkGenData = chunkGenDataOptional.get();
                    OptionalValue<BiomeSurfaceMaterialData> surfaceMaterialDataOptional = chunkGenData.surfaceMaterial;
                    if(surfaceMaterialDataOptional.isPresent()) {
                        BiomeSurfaceMaterialData surfaceMaterialData = surfaceMaterialDataOptional.get();
                        if(topBlockState != BlockWater.PROPERTIES.getBlockState()) {
                            chunk.setBlockState(x, y, z, Registries.BLOCKSTATE.get(surfaceMaterialData.topBlock));
                            chunk.setBlockState(x, y-1, z, Registries.BLOCKSTATE.get(surfaceMaterialData.midBlock));
                            chunk.setBlockState(x, y-2, z, Registries.BLOCKSTATE.get(surfaceMaterialData.midBlock));
                        } else {
                            if(chunkGenData.hasFrozenOceanSurface) {
                                chunk.setBlockState(x, y, z, BlockIce.PROPERTIES.getDefaultState());
                            } else {
                                int depth = 0;
                                while (topBlockState == BlockWater.PROPERTIES.getBlockState()) {
                                    topBlockState = chunk.getBlockState(x, y - (++depth), z);
                                }
                                for (int i = 0; i < surfaceMaterialData.seaFloorDepth; i++) {
                                    chunk.setBlockState(x, (y - depth) - i, z, Registries.BLOCKSTATE.get(surfaceMaterialData.seaFloorBlock));
                                }
                            }
                        }
                    } else {
                        log.warn("No surfaceMaterial for biome " + definition.getName());
                    }
                    OptionalValue<BiomeConsolidatedFeatureData[]> consolidatedFeatureDataOptional = chunkGenData.consolidatedFeatures;
                    if(consolidatedFeatureDataOptional.isPresent()) {
                        BiomeConsolidatedFeatureData[] consolidatedFeatureDataArray = consolidatedFeatureDataOptional.get();
                        for(BiomeConsolidatedFeatureData consolidatedFeatureData : consolidatedFeatureDataArray) {
                            System.out.println(definition.getName() + " " + Registries.BIOME.getFromBiomeStringList(consolidatedFeatureData.feature));
                        }
                    }
                } else {
                    log.warn("No chunkGenData for biome " + definition.getName());
                }
            }
        }
    }

    @Override
    public String name() {
        return NAME;
    }
}
