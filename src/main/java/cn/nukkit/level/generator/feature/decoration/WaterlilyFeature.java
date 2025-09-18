package cn.nukkit.level.generator.feature.decoration;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.BlockWaterlily;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.generator.ChunkGenerateContext;
import cn.nukkit.level.generator.feature.CountGenerateFeature;
import cn.nukkit.utils.random.NukkitRandom;
import cn.nukkit.utils.random.RandomSourceProvider;

import static cn.nukkit.level.generator.stages.normal.NormalTerrainStage.SEA_LEVEL;

public class WaterlilyFeature extends CountGenerateFeature {

    protected final static BlockState WATERLILY = BlockWaterlily.PROPERTIES.getDefaultState();

    public static final String NAME = "minecraft:fixup_waterlily_position_feature";


    @Override
    public String name() {
        return NAME;
    }

    @Override
    public int getBase() {
        return 4;
    }

    @Override
    public int getRandom() {
        return 2;
    }

    @Override
    public void populate(ChunkGenerateContext context, RandomSourceProvider random) {
        IChunk chunk = context.getChunk();
        int x = random.nextInt(15);
        int z = random.nextInt(15);
        int y = chunk.getHeightMap(x, z);
        if(y == SEA_LEVEL) {
            if(chunk.getBlockState(x, y, z).getIdentifier().equals(Block.WATER)) {
                chunk.setBlockState(x, y+1, z, WATERLILY);
            }
        }
    }
}
