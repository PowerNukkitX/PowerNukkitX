package org.powernukkitx.level.generator.stages.normal;

import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.BlockWater;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.generator.ChunkGenerateContext;
import org.powernukkitx.level.generator.GenerateStage;
import org.powernukkitx.level.generator.holder.NormalObjectHolder;
import org.powernukkitx.level.generator.noise.minecraft.noise.NormalNoise;
import org.powernukkitx.math.NukkitMath;
import org.powernukkitx.registry.Registries;
import it.unimi.dsi.fastutil.Pair;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionChunkGenData;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionData;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeSurfaceBuilderData;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeSurfaceMaterialAdjustmentData;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeSurfaceMaterialData;

@Slf4j
public class NormalSurfaceDataStage extends GenerateStage {

    public static final String NAME = "normal_surface";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();

        NormalNoise noise = ((NormalObjectHolder) chunk.getLevel().getGeneratorObjectHolder()).getSurfaceHolder().getNoise();
        chunk.batchProcess(unsafeChunk -> {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int y = unsafeChunk.getHeightMap(x, z);
                    BlockState topBlockState = unsafeChunk.getBlockState(x, y, z);
                    Pair<Short, BiomeDefinitionData> definition = Registries.BIOME.get(unsafeChunk.getBiomeId(x, y, z));
                    BiomeDefinitionData biome = definition.second();
                    BiomeDefinitionChunkGenData chunkGenData = biome.getChunkGenData();
                    if (chunkGenData != null) {
                        BiomeSurfaceBuilderData surfaceBuilderData = chunkGenData.getSurfaceBuilderData();
                        BiomeSurfaceMaterialData surfaceMaterialData = surfaceBuilderData == null ? null : surfaceBuilderData.getSurfaceMaterial();
                        if (surfaceMaterialData != null
                                && surfaceMaterialData.getTopBlock() != null
                                && surfaceMaterialData.getMidBlock() != null
                                && surfaceMaterialData.getSeaFloorBlock() != null) {
                            int topBlock = surfaceMaterialData.getTopBlock().getRuntimeId();
                            int midBlock = surfaceMaterialData.getMidBlock().getRuntimeId();
                            int seaFloorBlock = surfaceMaterialData.getSeaFloorBlock().getRuntimeId();


                            BiomeSurfaceMaterialAdjustmentData biomeSurfaceMaterialAdjustmentData = chunkGenData.getSurfaceMaterialAdjustment();
                            if(biomeSurfaceMaterialAdjustmentData != null) {
                                float random = noise.getValue(((unsafeChunk.getX() << 4) + x),  0, ((unsafeChunk.getZ() << 4) + z));
                                for(var element : biomeSurfaceMaterialAdjustmentData.getBiomeElements()) {
                                    if(random < element.getNoiseUpperBound() && random > element.getNoiseLowerBound()) {
                                        int _topBlock = element.getAdjustedMaterials().getTopBlock().getRuntimeId();
                                        int _midBlock = element.getAdjustedMaterials().getMidBlock().getRuntimeId();
                                        int _seaFloorBlock = element.getAdjustedMaterials().getSeaFloorBlock().getRuntimeId();
                                        if(_topBlock != -1) topBlock = _topBlock;
                                        if(_midBlock != -1) midBlock = _midBlock;
                                        if(_seaFloorBlock != -1) seaFloorBlock = _seaFloorBlock;
                                    }
                                }
                            }
                            if (topBlockState != BlockWater.PROPERTIES.getBlockState()) {
                                unsafeChunk.setBlockState(x, y, z, Registries.BLOCKSTATE.get(topBlock), 0);
                                BlockState midState = Registries.BLOCKSTATE.get(midBlock);
                                float midDepth = NukkitMath.remapFromNormalized(noise.getValue(x, 0, z), 1, 4);
                                for(int i = 1; i < midDepth; i++){
                                    unsafeChunk.setBlockState(x, y-i, z, midState, 0);
                                }
                            } else {
                                int depth = 0;
                                while (topBlockState == BlockWater.PROPERTIES.getBlockState()) {
                                    topBlockState = unsafeChunk.getBlockState(x, y - (++depth), z);
                                }
                                if (depth > surfaceMaterialData.getSeaFloorDepth()) {
                                    BlockState state = Registries.BLOCKSTATE.get(seaFloorBlock);
                                    unsafeChunk.setBlockState(x, (y - depth), z, state, 0);
                                } else {
                                    BlockState midState = Registries.BLOCKSTATE.get(midBlock);
                                    for (int i = 0; i < 3; i++) {
                                        unsafeChunk.setBlockState(x, (y - depth) - i, z, midState, 0);
                                    }
                                }
                            }
                        }
                    } else {
                        log.warn("No chunkGenData for biome {}", definition.first());
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
