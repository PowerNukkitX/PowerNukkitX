package cn.nukkit.level.generator.stages.normal;

import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockWater;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.holder.NormalObjectHolder;
import cn.nukkit.level.generator.noise.minecraft.noise.NormalNoise;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.registry.Registries;
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
                                for(var element : biomeSurfaceMaterialAdjustmentData.getBiomeElements()) {
                                    float random = noise.getValue(((unsafeChunk.getX() << 4) + x),  0, ((unsafeChunk.getZ() << 4) + z));
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
                                for(int i = 1; i < NukkitMath.remapFromNormalized(noise.getValue(x, 0, z), 1, 4); i++){
                                    unsafeChunk.setBlockState(x, y-i, z, Registries.BLOCKSTATE.get(midBlock), 0);
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
                                    for (int i = 0; i < 3; i++) {
                                        unsafeChunk.setBlockState(x, (y - depth) - i, z, Registries.BLOCKSTATE.get(midBlock), 0);
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
