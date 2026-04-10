package cn.nukkit.level.generator.stages.normal;

import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockWater;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.holder.NormalObjectHolder;
import cn.nukkit.level.generator.noise.f.SimplexF;
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
        SimplexF simplexF = ((NormalObjectHolder) chunk.getLevel().getGeneratorObjectHolder()).getSurfaceHolder().getSimplexF();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int y = chunk.getHeightMap(x, z);
                BlockState topBlockState = chunk.getBlockState(x, y, z);
                Pair<Short, BiomeDefinitionData> definition = Registries.BIOME.get(chunk.getBiomeId(x, y, z));
                BiomeDefinitionData biome = definition.second();
                BiomeDefinitionChunkGenData chunkGenData = biome.getChunkGenData();
                if (chunkGenData != null) {
                    BiomeSurfaceBuilderData surfaceBuilderData = chunkGenData.getSurfaceBuilderData();
                    BiomeSurfaceMaterialData surfaceMaterialData = surfaceBuilderData.getSurfaceMaterial();
                    if (surfaceMaterialData != null) {
                        int topBlock = surfaceMaterialData.getTopBlock().getRuntimeId();
                        int midBlock = surfaceMaterialData.getMidBlock().getRuntimeId();
                        int seaFloorBlock = surfaceMaterialData.getSeaFloorBlock().getRuntimeId();

                        BiomeSurfaceMaterialAdjustmentData biomeSurfaceMaterialAdjustmentData = chunkGenData.getSurfaceMaterialAdjustment();
                        if (biomeSurfaceMaterialAdjustmentData != null) {
                            for (var element : biomeSurfaceMaterialAdjustmentData.getBiomeElements()) {
                                float random = simplexF.noise2D(((chunk.getX() << 4) + x) * 0.25f, ((chunk.getZ() << 4) + z) * 0.25f, true);
                                if (random < element.getNoiseUpperBound() && random > element.getNoiseLowerBound()) {
                                    int _topBlock = element.getAdjustedMaterials().getTopBlock().getRuntimeId();
                                    int _midBlock = element.getAdjustedMaterials().getMidBlock().getRuntimeId();
                                    int _seaFloorBlock = element.getAdjustedMaterials().getSeaFloorBlock().getRuntimeId();
                                    if (_topBlock != -1) topBlock = _topBlock;
                                    if (_midBlock != -1) midBlock = _midBlock;
                                    if (_seaFloorBlock != -1) seaFloorBlock = _seaFloorBlock;
                                }

                            }
                        }
                        if (topBlockState != BlockWater.PROPERTIES.getBlockState()) {
                            chunk.setBlockState(x, y, z, Registries.BLOCKSTATE.get(topBlock));
                            for (int i = 1; i < NukkitMath.remapFromNormalized(simplexF.noise2D(x, z, true), 1, 4); i++) {
                                chunk.setBlockState(x, y - i, z, Registries.BLOCKSTATE.get(midBlock));
                            }
                        } else {
                            int depth = 0;
                            while (topBlockState == BlockWater.PROPERTIES.getBlockState()) {
                                topBlockState = chunk.getBlockState(x, y - (++depth), z);
                            }
                            if (depth > surfaceMaterialData.getSeaFloorDepth()) {
                                BlockState state = Registries.BLOCKSTATE.get(seaFloorBlock);
                                chunk.setBlockState(x, (y - depth), z, state);
                            } else {
                                for (int i = 0; i < 3; i++) {
                                    chunk.setBlockState(x, (y - depth) - i, z, Registries.BLOCKSTATE.get(midBlock));
                                }
                            }
                        }
                    }
                } else {
                    log.warn("No chunkGenData for biome {}", definition.first());
                }
            }
        }
    }

    @Override
    public String name() {
        return NAME;
    }
}
