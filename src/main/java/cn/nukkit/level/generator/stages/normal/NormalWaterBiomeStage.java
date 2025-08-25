package cn.nukkit.level.generator.stages.normal;

import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockWater;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.format.ChunkState;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateStage;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;
import cn.nukkit.registry.Registries;

import java.util.Set;

import static cn.nukkit.tags.BiomeTags.*;

public class NormalWaterBiomeStage extends GenerateStage {

    public static final String NAME = "normal_waterbiome";

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        Level level = chunk.getLevel();
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                int y = chunk.recalculateHeightMapColumn(x, z);
                BlockState topBlockState = chunk.getBlockState(x, y, z);
                if(topBlockState == BlockWater.PROPERTIES.getDefaultState()) {
                    BiomeDefinition definition = Registries.BIOME.get(chunk.getBiomeId(x, y, z));
                    Set<String> tags = definition.getTags();
                    if(!tags.contains(OCEAN) && !tags.contains(SWAMP)) {
                        int biomeId = BiomeID.RIVER;
                        if(tags.contains(FROZEN)) {
                            biomeId = BiomeID.FROZEN_RIVER;
                        }
                        final int minHeight = level.getMinHeight();
                        final int maxHeight = level.getMaxHeight();
                        for(int i = minHeight; i < maxHeight; i++) {
                            chunk.setBiomeId(x, i, z, biomeId);
                        }
                    }
                }
            }
        }
        chunk.setChunkState(ChunkState.GENERATED);
    }

    @Override
    public String name() {
        return NAME;
    }
}
