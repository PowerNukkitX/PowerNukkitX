package cn.nukkit.level.generator.stages.normal;

import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockWater;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.holder.NormalObjectHolder;
import cn.nukkit.level.generator.noise.f.SimplexF;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.network.protocol.types.biome.BiomeDefinitionChunkGenData;
import cn.nukkit.network.protocol.types.biome.BiomeDefinitionData;
import cn.nukkit.network.protocol.types.biome.BiomeSurfaceMaterialAdjustmentData;
import cn.nukkit.network.protocol.types.biome.BiomeSurfaceMaterialData;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.OptionalValue;
import cn.nukkit.utils.random.NukkitRandom;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NormalSurfaceDataStage extends GenerateStage {

    public static final String NAME = "normal_surface";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();

        SimplexF simplexF = ((NormalObjectHolder) chunk.getLevel().getGeneratorObjectHolder()).getSurfaceHolder().getSimplexF();
        chunk.batchProcess(unsafeChunk -> {
            for(int x = 0; x < 16; x++) {
                for(int z = 0; z < 16; z++) {
                    int y = unsafeChunk.getHeightMap(x, z);
                    BlockState topBlockState = unsafeChunk.getBlockState(x, y, z);
                    BiomeDefinition definition = Registries.BIOME.get(unsafeChunk.getBiomeId(x, y, z));
                    BiomeDefinitionData biome = definition.data;
                    OptionalValue<BiomeDefinitionChunkGenData> chunkGenDataOptional = biome.chunkGenData;
                    if(chunkGenDataOptional.isPresent()) {
                        BiomeDefinitionChunkGenData chunkGenData = chunkGenDataOptional.get();

                        OptionalValue<BiomeSurfaceMaterialData> surfaceMaterialDataOptional = chunkGenData.surfaceMaterial;
                        if(surfaceMaterialDataOptional.isPresent()) {
                            BiomeSurfaceMaterialData surfaceMaterialData = surfaceMaterialDataOptional.get();
                            int topBlock = surfaceMaterialData.topBlock;
                            int midBlock = surfaceMaterialData.midBlock;
                            int seaFloorBlock = surfaceMaterialData.seaFloorBlock;


                            OptionalValue<BiomeSurfaceMaterialAdjustmentData> biomeSurfaceMaterialAdjustmentDataOptional = chunkGenData.surfaceMaterialAdjustment;
                            if(biomeSurfaceMaterialAdjustmentDataOptional.isPresent()) {
                                BiomeSurfaceMaterialAdjustmentData biomeSurfaceMaterialAdjustmentData = biomeSurfaceMaterialAdjustmentDataOptional.get();
                                for(var element : biomeSurfaceMaterialAdjustmentData.biomeElements) {
                                    float random = simplexF.noise2D(((unsafeChunk.getX() << 4) + x) * 0.25f, ((unsafeChunk.getZ() << 4) + z) * 0.25f, true);
                                    if(random < element.noiseUpperBound && random > element.noiseLowerBound) {
                                        int _topBlock = element.adjustedMaterials.topBlock;
                                        int _midBlock = element.adjustedMaterials.midBlock;
                                        int _seaFloorBlock = element.adjustedMaterials.seaFloorBlock;
                                        if(_topBlock != -1) topBlock = _topBlock;
                                        if(_midBlock != -1) midBlock = _midBlock;
                                        if(_seaFloorBlock != -1) seaFloorBlock = _seaFloorBlock;
                                    }

                                }
                            }
                            if(topBlockState != BlockWater.PROPERTIES.getBlockState()) {
                                unsafeChunk.setBlockState(x, y, z, Registries.BLOCKSTATE.get(topBlock), 0);
                                for(int i = 1; i < NukkitMath.remapFromNormalized(simplexF.noise2D(x, z, true), 1, 4); i++){
                                    unsafeChunk.setBlockState(x, y-i, z, Registries.BLOCKSTATE.get(midBlock), 0);
                                }
                            } else {
                                int depth = 0;
                                while (topBlockState == BlockWater.PROPERTIES.getBlockState()) {
                                    topBlockState = unsafeChunk.getBlockState(x, y - (++depth), z);
                                }
                                if(depth > surfaceMaterialData.seaFloorDepth) {
                                    BlockState state = Registries.BLOCKSTATE.get(seaFloorBlock);
                                    unsafeChunk.setBlockState(x, (y - depth), z, state, 0);
                                } else {
                                    for (int i = 0; i < 3; i++) {
                                        unsafeChunk.setBlockState(x, (y - depth) - i, z, Registries.BLOCKSTATE.get(midBlock), 0);
                                    }
                                }
                            }
                        }
                    } else {
                        log.warn("No chunkGenData for biome {}", definition.getName());
                    }
                }
            }
        });
    }

    @Override
    public String name() {
        return NAME;
    }
}
