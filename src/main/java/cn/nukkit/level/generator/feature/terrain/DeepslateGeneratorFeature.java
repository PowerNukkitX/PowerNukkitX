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


    }
}
