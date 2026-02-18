package cn.nukkit.level.generator.stages.normal;

import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockWater;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.level.generator.noise.f.SimplexF;
import cn.nukkit.math.NukkitMath;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionData;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionChunkGenData;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeSurfaceMaterialAdjustmentData;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeSurfaceMaterialData;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.random.NukkitRandom;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NormalSurfaceDataStage extends GenerateStage {

    public static final String NAME = "normal_surface";

    private SimplexF simplexF;


    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();

        if(simplexF == null) simplexF = new SimplexF(new NukkitRandom(chunk.getLevel().getSeed()), 1f, 2 / 4f, 1 / 10f);
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                int y = chunk.getHeightMap(x, z);
                BlockState topBlockState = chunk.getBlockState(x, y, z);
                BiomeDefinitionData definition = Registries.BIOME.get(chunk.getBiomeId(x, y, z));
                BiomeDefinitionChunkGenData chunkGenData = definition.getChunkGenData();
                if(chunkGenData != null) {
                    BiomeSurfaceMaterialData surfaceMaterialData = chunkGenData.getSurfaceMaterial();
                    if(surfaceMaterialData != null) {
                        int topBlock = surfaceMaterialData.getTopBlock().getRuntimeId();
                        int midBlock = surfaceMaterialData.getMidBlock().getRuntimeId();
                        int seaFloorBlock = surfaceMaterialData.getSeaFloorBlock().getRuntimeId();

                        BiomeSurfaceMaterialAdjustmentData biomeSurfaceMaterialAdjustmentData = chunkGenData.getSurfaceMaterialAdjustment();
                        if(biomeSurfaceMaterialAdjustmentData != null) {
                            for(var element : biomeSurfaceMaterialAdjustmentData.getBiomeElements()) {
                                float random = simplexF.noise2D(((chunk.getX() << 4) + x) * 0.25f, ((chunk.getZ() << 4) + z) * 0.25f, true);
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
                        if(topBlockState != BlockWater.PROPERTIES.getBlockState()) {
                            chunk.setBlockState(x, y, z, Registries.BLOCKSTATE.get(topBlock));
                            for(int i = 1; i < NukkitMath.remap(simplexF.noise2D(x, z, true), -1, 1, 1, 4); i++){
                                chunk.setBlockState(x, y-i, z, Registries.BLOCKSTATE.get(midBlock));
                            }
                        } else {
                            int depth = 0;
                            while (topBlockState == BlockWater.PROPERTIES.getBlockState()) {
                                topBlockState = chunk.getBlockState(x, y - (++depth), z);
                            }
                            if(depth > surfaceMaterialData.getSeaFloorDepth()) {
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
                    log.warn("No chunkGenData for biome {}", definition.getId());
                }
            }
        }
    }

    @Override
    public String name() {
        return NAME;
    }
}
