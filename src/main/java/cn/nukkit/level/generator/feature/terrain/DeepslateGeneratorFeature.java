package cn.nukkit.level.generator.feature.terrain;

import cn.nukkit.block.BlockBedrock;
import cn.nukkit.block.BlockDeepslate;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.GenerateFeature;
import cn.nukkit.utils.random.NukkitRandom;

public class DeepslateGeneratorFeature extends GenerateFeature {

    public static final String NAME = "minecraft:overworld_underground_deepslate_feature";


    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void apply(ChunkGenerateContext context) {
        IChunk chunk = context.getChunk();
        Level level = chunk.getLevel();
        NukkitRandom random = new NukkitRandom(level.getSeed());

        NukkitRandom bedrockRandom = random.identical();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for(int y = level.getMinHeight(); y < 0; y++) {
                    chunk.setBlockState(x, y, z, BlockDeepslate.PROPERTIES.getDefaultState());
                }
                for (int y = 0; y < 8; y++) {
                    if (random.nextBoundedInt(y) == 0) {
                        chunk.setBlockState(x, y, z, BlockDeepslate.PROPERTIES.getDefaultState());
                    }
                }
                chunk.setBlockState(x, level.getMinHeight(), z, BlockBedrock.PROPERTIES.getDefaultState());
                for (int i = 1; i < 5; i++) {
                    if (bedrockRandom.nextBoundedInt(i) == 0) {
                        chunk.setBlockState(x, level.getMinHeight() +i, z, BlockBedrock.PROPERTIES.getDefaultState());
                    }
                }
            }
        }
    }
}
